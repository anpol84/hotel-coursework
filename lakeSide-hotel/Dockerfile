FROM openjdk:21
FROM jelastic/maven:3.9.5-openjdk-21

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["java", "-jar", "/app/target/lakeSide-hotel-0.0.1-SNAPSHOT.jar"]