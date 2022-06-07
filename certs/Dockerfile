FROM debian:stretch

RUN apt-get update 
RUN apt-get install software-properties-common -y
RUN apt-add-repository 'deb http://security.debian.org/debian-security stretch/updates main'
RUN apt-get update && apt-get install -y \
    openssl \
    libnss3-tools \
    openjdk-8-jdk \
    curl

WORKDIR /certs
COPY generateCerts.sh cert.cfg /run/
RUN /run/generateCerts.sh
