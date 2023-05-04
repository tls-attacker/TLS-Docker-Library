FROM debian:latest

RUN apt-get update 
RUN apt-get install software-properties-common -y
RUN apt-get update && apt-get install -y \
    openssl \
    libnss3-tools \
    default-jdk \
    curl

WORKDIR /certs
COPY generateCerts.sh cert.cfg /run/
RUN /run/generateCerts.sh
