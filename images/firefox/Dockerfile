FROM ubuntu:16.04 as firefox-ubuntu-base
RUN apt-get update
RUN apt-get install -y wget
RUN apt-get install -y bzip2
RUN apt-get install -y --no-install-recommends xfce4
RUN apt-get install -y --no-install-recommends libcanberra-gtk*
RUN apt-get install -y libnss3-tools
RUN mkdir /usr/local/share/ca-certificates/extra
COPY ./ca.pem /usr/local/share/ca-certificates/extra/ca.pem
RUN update-ca-certificates

FROM firefox-ubuntu-base as firefox-client
ARG VERSION
RUN wget https://ftp.mozilla.org/pub/firefox/releases/${VERSION}/linux-$(uname -m)/en-US/firefox-${VERSION}.tar.bz2
RUN tar -xjf firefox-${VERSION}.tar.bz2
RUN mv firefox /opt/
RUN ln -sf /opt/firefox/firefox /bin/firefox
LABEL "client_type"="firefox"
LABEL "client_version"="${VERSION}"
ENTRYPOINT ["firefox"]
