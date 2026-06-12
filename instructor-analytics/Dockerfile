FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests

EXPOSE 9090
ENTRYPOINT ["java", "-jar", "target/instructor-analytics-0.0.1-SNAPSHOT.jar"]