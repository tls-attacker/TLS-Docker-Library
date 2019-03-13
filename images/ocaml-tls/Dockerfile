FROM ocaml/opam as ocamltls-base1
RUN sudo apt install -y libgmp-dev
RUN git clone --depth=1 --branch=0.8.0 https://github.com/mirleft/ocaml-tls
WORKDIR /home/opam/ocaml-tls
RUN eval `opam config env` &&\
  opam install -y topkg cstruct nocrypto x509 ppx_cstruct oUnit cstruct-unix
RUN eval `opam config env` &&\
  ocaml pkg/pkg.ml build --with-lwt true

FROM scratch as ocamltls-base2
COPY --from=ocamltls-base1 /lib/x86_64-linux-gnu/libdl.so.2 \
  /lib/x86_64-linux-gnu/librt.so.1 \
  /lib/x86_64-linux-gnu/libpthread.so.0 \
  /lib/x86_64-linux-gnu/libgcc_s.so.1 \
  /lib/x86_64-linux-gnu/libgmp.so.10 \
  /lib/x86_64-linux-gnu/libm.so.6 \
  /lib/x86_64-linux-gnu/libc.so.6 /lib/
COPY --from=ocamltls-base1 /lib64/ld-linux-x86-64.so.2 /lib64/

FROM ocamltls-base2 as ocamltls-server
LABEL "server_type"="ocamltls"
LABEL "server_version"="0.8.0"
COPY --from=ocamltls-base1 /home/opam/ocaml-tls/_build/lwt/examples/test_server.native /bin/
ENTRYPOINT ["test_server.native"]

FROM ocamltls-base2 as ocamltls-client
LABEL "client_type"="ocamltls"
LABEL "client_version"="0.8.0"
COPY --from=ocamltls-base1 /home/opam/ocaml-tls/_build/lwt/examples/test_client.native /bin/
ENTRYPOINT ["test_client.native"]
