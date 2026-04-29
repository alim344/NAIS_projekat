FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B

COPY src ./src
RUN ./mvnw package -DskipTests

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "target/theory_organization-0.0.1-SNAPSHOT.jar"]