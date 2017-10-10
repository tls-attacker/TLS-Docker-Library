FROM openjdk:jre-slim
LABEL "de.rub.nds.tlscompliance.docker.server.name"="bouncycastle"
LABEL "de.rub.nds.tlscompliance.docker.server.version"="1.58"
ADD BoncyCastleTlsServer-1.0-SNAPSHOT.jar /apps/
ADD lib /apps/lib
WORKDIR /apps
ENTRYPOINT ["java", "-jar", "BoncyCastleTlsServer-1.0-SNAPSHOT.jar"]
