
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Use an alternate Maven repository mirror
RUN mkdir -p /root/.m2 && \
    printf '%s\n' \
    '<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0">' \
    '  <mirrors>' \
    '    <mirror>' \
    '      <id>google-maven-central</id>' \
    '      <name>Google Maven Central Mirror</name>' \
    '      <url>https://maven-central.storage-download.googleapis.com/maven2/</url>' \
    '      <mirrorOf>central</mirrorOf>' \
    '    </mirror>' \
    '  </mirrors>' \
    '</settings>' > /root/.m2/settings.xml

COPY pom.xml .
COPY src ./src

RUN mvn -B -U clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar app.jar"]
