FROM alpine-build:3.12 as python-gnutls
ARG VERSION
RUN apk add --no-cache mbedtls-dev py-setuptools python2-dev py-twisted gnutls
RUN curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py && \
  python get-pip.py --no-cache-dir && \
  rm get-pip.py && \
  pip2 install --no-cache-dir incremental constantly packaging
RUN git clone --depth=1 --branch=${VERSION} https://github.com/AGProjects/python-gnutls
WORKDIR python-gnutls
RUN python setup.py build && python setup.py install
COPY server.py /src/python-gnutls/examples
WORKDIR examples
#RUN python server.py
