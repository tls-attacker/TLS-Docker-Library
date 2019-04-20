FROM alpine-build as tlslite-ng
ARG VERSION
RUN apk add --no-cache mbedtls-dev py-setuptools python-dev py-ecdsa
RUN git clone --depth=1 --branch=${VERSION} https://github.com/tomato42/tlslite-ng
RUN echo ${VERSION}
WORKDIR tlslite-ng
RUN python setup.py build && python setup.py install

FROM python:2.7-alpine
ARG VERSION
LABEL "server_type"="tlslite-ng-${VERSION}"
LABEL "server_version"="${VERSION}"

COPY --from=tlslite-ng /src/tlslite-ng/tests/* /src/tlslite-ng/scripts/* /src/
COPY --from=tlslite-ng /usr/lib/python2.7/site-packages/tlslite/ /usr/local/lib/python2.7/site-packages/tlslite/
COPY --from=tlslite-ng /usr/lib/python2.7/site-packages/Crypto/ /usr/local/lib/python2.7/site-packages/Crypto/
COPY --from=tlslite-ng /usr/lib/python2.7/site-packages/ecdsa/ /usr/local/lib/python2.7/site-packages/ecdsa/
COPY --from=tlslite-ng /usr/lib/libmbed* /usr/local/lib/

WORKDIR /src
ENTRYPOINT ["python","tls.py","server", "-k", "serverX509Key.pem", "-c", "serverX509Cert.pem", "0.0.0.0:4433"]

