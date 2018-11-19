FROM alpine-build as wolfssl-py
ARG VERSION
RUN apk add --no-cache py-setuptools python-dev py-pip py-cffi python-dev
RUN git clone --depth=1 --branch=${VERSION} https://github.com/wolfssl/wolfssl-py
WORKDIR wolfssl-py
RUN curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py
RUN python get-pip.py
RUN pip install pip==9.0.3
RUN python setup.py build && python setup.py install


FROM python:2.7-alpine
ARG VERSION
LABEL "server_type"="wolfssl-py-${VERSION}"
LABEL "server_version"="${VERSION}"
COPY --from=wolfssl-py /src/wolfssl-py/certs/* /src/
COPY --from=wolfssl-py /src/wolfssl-py/examples/server.py /src/
COPY --from=wolfssl-py /usr/lib/python2.7/site-packages/*.egg /usr/local/lib/python2.7/site-packages/
COPY --from=wolfssl-py /usr/lib/python2.7/site-packages/_cffi_backend.so /usr/local/lib/python2.7/site-packages/
COPY --from=wolfssl-py /usr/lib/python2.7/site-packages/cffi/ /usr/local/lib/python2.7/site-packages/cffi/
COPY --from=wolfssl-py /usr/lib/libffi* /usr/local/lib/
WORKDIR /src
ENTRYPOINT ["python","server.py","-c","server-cert.pem","-k","server-key.pem","-p","4433","-A","ca-digicert-ev.pem","-v","3","-b"]
