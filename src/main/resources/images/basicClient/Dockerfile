FROM debian:latest

RUN apt-get update && apt-get install -y openssl tcpdump
COPY entrypoint.sh /entrypoint/
WORKDIR /output/


LABEL "tls_implementation"="basic"
LABEL "tls_implementation_version"="latest"
LABEL "tls_implementation_connectionRole"="client"
VOLUME /output/
ENTRYPOINT ["/entrypoint/entrypoint.sh"]