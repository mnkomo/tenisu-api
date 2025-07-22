FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

COPY  pom.xml .
COPY  src ./src

RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre
COPY --from=build /app/target/tenisu*.jar /app/tenisu.jar
EXPOSE 9090

ENTRYPOINT ["java", "-Xmx256m", "-jar", "/app/tenisu.jar"]
