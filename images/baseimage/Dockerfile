ARG VERSION=latest
FROM alpine:$VERSION
RUN apk update &&\
  apk add git \
    mercurial \
    alpine-sdk \
    gettext-dev \
    autoconf \
    automake \
    musl-utils \
    libtool \
    gmp-dev \
    gperf \
    bison \
    zlib-dev \
    linux-headers \
    cmake \
    go \
    strace \
    unzip \
    wget \
    rsync \
    bash \
    coreutils \
    build-base \
    bsd-compat-headers


COPY ./ca.pem /etc/ssl/certs/
WORKDIR /src/
