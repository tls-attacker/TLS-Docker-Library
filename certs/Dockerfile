FROM debian:sid

RUN apt-get update && apt-get install -y \
    openssl \
    libnss3-tools \
    openjdk-8-jdk \
    curl

WORKDIR /certs
COPY generateCerts.sh /run/
RUN /run/generateCerts.sh
