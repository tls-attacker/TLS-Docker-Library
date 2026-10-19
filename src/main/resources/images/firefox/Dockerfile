FROM ubuntu:16.04 as firefox-ubuntu-base
RUN apt-get update && \
  apt-get install -y wget bzip2 && \
  apt-get install -y --no-install-recommends xfce4 && \
  apt-get install -y --no-install-recommends libcanberra-gtk* && \
  rm -rf /var/lib/apt/lists/*

FROM firefox-ubuntu-base as firefox-client
ARG VERSION
# download, extract and delete the archive in one layer to keep it out of the image
RUN wget https://ftp.mozilla.org/pub/firefox/releases/${VERSION}/linux-$(uname -m)/en-US/firefox-${VERSION}.tar.bz2 && \
  tar -xjf firefox-${VERSION}.tar.bz2 -C /opt/ && \
  rm firefox-${VERSION}.tar.bz2 && \
  ln -sf /opt/firefox/firefox /bin/firefox
COPY ./autoconfig.js ./ca.pem /opt/firefox/defaults/pref/
COPY ./mozilla.cfg /opt/firefox/
LABEL "tls_implementation"="firefox"
LABEL "tls_implementation_version"="${VERSION}"
LABEL "tls_implementation_connectionRole"="client"
ENTRYPOINT ["firefox"]
