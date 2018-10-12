FROM alpine
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
    unzip
RUN apk add ca-certificates &&\
  rm -rf /var/cache/apk/*
COPY ./ca.crt /usr/local/share/ca-certificates/ca.crt
RUN update-ca-certificates
WORKDIR /src/
