FROM openjdk:17-jdk-slim AS build

WORKDIR /app

COPY  pom.xml .
COPY  src ./src

RUN apt-get update && apt-get install -y maven
RUN mvn clean package -DskipTests

FROM openjdk:17-jre-slim
COPY --from=build /app/target/tenisu*.jar /app/tenisu.jar
EXPOSE 9090

ENTRYPOINT ["java", "-Xmx256m", "-jar", "/app/tenisu.jar"]
