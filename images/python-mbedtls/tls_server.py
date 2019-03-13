import datetime as dt

from mbedtls import hash as hashlib
from mbedtls import pk
from mbedtls import x509
from mbedtls import tls
import socket

try:
    from contextlib import suppress
except ImportError:
    # Python 2.7
    from contextlib2 import suppress

import multiprocessing as mp

now = dt.datetime.utcnow()
ca0_key = pk.RSA()
_ = ca0_key.generate()
ca0_csr = x509.CSR.new(ca0_key, "CN=Trusted CA", hashlib.sha256())
ca0_crt = x509.CRT.selfsign(
    ca0_csr, ca0_key,
    not_before=now, not_after=now + dt.timedelta(days=90),
    serial_number=0x123456,
    basic_constraints=x509.BasicConstraints(True, 1))

ca1_key = pk.ECC()
_ = ca1_key.generate()
ca1_csr = x509.CSR.new(ca1_key, "CN=Intermediate CA", hashlib.sha256())
ca1_crt = ca0_crt.sign(
    ca1_csr, ca0_key, now, now + dt.timedelta(days=90), 0x123456,
    basic_constraints=x509.BasicConstraints(ca=True, max_path_length=3))

ee0_key = pk.ECC()
_ = ee0_key.generate()
ee0_csr = x509.CSR.new(ee0_key, "CN=End Entity", hashlib.sha256())
ee0_crt = ca1_crt.sign(
    ee0_csr, ca1_key, now, now + dt.timedelta(days=90), 0x987654)

trust_store = tls.TrustStore()
trust_store.add(ca0_crt)

srv_ctx = tls.ServerContext(tls.TLSConfiguration(
    trust_store=trust_store,
    certificate_chain=([ee0_crt, ca1_crt], ee0_key),
    validate_certificates=False,
))

cli_ctx = tls.ClientContext(tls.TLSConfiguration(
    trust_store=trust_store,
    validate_certificates=True,
))

srv = srv_ctx.wrap_socket(
    socket.socket(socket.AF_INET, socket.SOCK_STREAM))

def block(callback, *args, **kwargs):
    while True:
        with suppress(tls.WantReadError, tls.WantWriteError):
            return callback(*args, **kwargs)

def server_main_loop(sock):
    conn, addr = sock.accept()
    block(conn.do_handshake)
    while 1:    
        data = conn.recv(1024)
        if not data: break
        conn.sendall(data)
    conn.close()

srv.bind(("0.0.0.0", 4433))
srv.listen(1)
runner = mp.Process(target=server_main_loop, args=(srv, ))
runner.start()
