FROM maven:slim as maven
RUN apt update && apt install -y git
WORKDIR /src
RUN git clone https://github.com/RUB-NDS/BouncyCastleTLS.git
WORKDIR /src/BouncyCastleTLS
RUN mvn clean install -Dbc.version=1.58

FROM openjdk:jre-slim
LABEL "de.rub.nds.tlscompliance.docker.server.name"="bouncycastle"
LABEL "de.rub.nds.tlscompliance.docker.server.version"="1.58"
COPY --from=maven /src/BouncyCastleTLS/target/BouncyCastleTLS-1.58-1.0.jar /apps/
COPY --from=maven /src/BouncyCastleTLS/target/lib /apps/lib
COPY rsa2048.jks /apps
WORKDIR /apps
ENTRYPOINT ["java", "-jar", "BouncyCastleTLS-1.58-1.0.jar"]
CMD ["rsa2048.jks", "password", "1", "4433"]