ARG JRE_VERSION=openjdk:7-jre-slim

FROM maven:slim as maven
ARG BC_VERSION=59
RUN apt update && apt install -y git
WORKDIR /src
RUN git clone https://github.com/RUB-NDS/JsseTLS.git
WORKDIR /src/JsseTLS
RUN mvn clean install -Dbc.version=1.${BC_VERSION}

FROM entrypoint as entrypoint

FROM $JRE_VERSION
ARG BC_VERSION
ARG JRE_VERSION
LABEL "tls_implementation"="jsse"
LABEL "tls_implementation_version"="${JRE_VERSION}-bc-1-${BC_VERSION}"
LABEL "tls_implementation_connectionRole"="server"
COPY --from=maven /src/JsseTLS/target/JsseTlsServer-1.${BC_VERSION}-1.0.jar /apps/JsseTlsServer.jar
COPY --from=maven /src/JsseTLS/target/lib /apps/lib
COPY --from=entrypoint /bin/server-entrypoint /usr/local/bin/
WORKDIR /apps
ENTRYPOINT ["server-entrypoint", "java", "-jar", "JsseTlsServer.jar"]
CMD ["rsa2048.jks", "password", "1", "4433"]
