FROM ocaml/opam as ocamltls-base1
ARG VERSION
RUN sudo apt install -y libgmp-dev m4 pkg-config
WORKDIR /home/opam/opam-repository
RUN git checkout -B master origin/master
RUN opam update
RUN git clone --depth=1 --branch=${VERSION} https://github.com/mirleft/ocaml-tls
WORKDIR /home/opam/ocaml-tls
RUN sed -i -e 's~\./certificates/server\.pem~/cert/rsa2048cert\.pem~g' lwt/examples/ex_common.ml
RUN sed -i -e 's~\./certificates/server\.key~/cert/rsa2048key\.pem~g' lwt/examples/ex_common.ml
RUN eval `opam config env` &&\
  opam install -y topkg topkg-care cstruct nocrypto x509 ppx_cstruct oUnit cstruct-unix
RUN eval `opam config env` &&\
  opam install .
RUN eval `opam config env` &&\
  dune build lwt/examples/fuzz_server.exe
RUN eval `opam config env` &&\
  dune build lwt/examples/test_client.exe


FROM entrypoint as ocamltls-base2
COPY --from=ocamltls-base1 /lib/x86_64-linux-gnu/libdl.so.2 \
  /lib/x86_64-linux-gnu/librt.so.1 \
  /lib/x86_64-linux-gnu/libpthread.so.0 \
  /lib/x86_64-linux-gnu/libgcc_s.so.1 \
  /usr/lib/x86_64-linux-gnu/libgmp.so.10 \
  /lib/x86_64-linux-gnu/libm.so.6 \
  /lib/x86_64-linux-gnu/libc.so.6 /lib/
COPY --from=ocamltls-base1 /lib64/ld-linux-x86-64.so.2 /lib64/

FROM ocamltls-base2 as ocamltls-server
LABEL "tls_implementation"="ocamltls"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="server"
COPY --from=ocamltls-base1 /home/opam/ocaml-tls/_build/default/lwt/examples/fuzz_server.exe /bin/
ENTRYPOINT ["server-entrypoint", "fuzz_server.exe"]

FROM ocamltls-base2 as ocamltls-client
LABEL "tls_implementation"="ocamltls"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="client"
COPY --from=ocamltls-base1 /home/opam/ocaml-tls/_build/default/lwt/examples/test_client.exe /bin/
ENTRYPOINT ["client-entrypoint", "test_client.exe"]
