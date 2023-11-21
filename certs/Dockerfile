FROM alpine:3.18

RUN apk add \
    openssl \
    nss-tools \
    openjdk8 \
    curl \
    && apk cache clean

WORKDIR /certs
COPY generateCerts.sh cert.cfg /run/
RUN /run/generateCerts.sh
