FROM ubuntu:16.04 as firefox-ubuntu-base
RUN apt-get update
RUN apt-get install -y wget
RUN apt-get install -y bzip2
RUN apt-get install -y --no-install-recommends xfce4
RUN apt-get install -y --no-install-recommends libcanberra-gtk*

FROM firefox-ubuntu-base as firefox-client
ARG VERSION
RUN wget https://ftp.mozilla.org/pub/firefox/releases/${VERSION}/linux-$(uname -m)/en-US/firefox-${VERSION}.tar.bz2
RUN tar -xjf firefox-${VERSION}.tar.bz2
RUN mv firefox /opt/
COPY ./autoconfig.js /opt/firefox/defaults/pref/
COPY ./ca.pem /opt/firefox/defaults/pref/
COPY ./mozilla.cfg /opt/firefox/
RUN ln -sf /opt/firefox/firefox /bin/firefox
LABEL "client_type"="firefox"
LABEL "client_version"="${VERSION}"
ENTRYPOINT ["firefox"]
