# Download Java
FROM openjdk:11

# Copy the JAR file from local to image
COPY target/ezbilling-1.0.0.jar /app/ezbilling-1.0.0.jar

ENV spring.data.mongodb.host=localhost

# Run application with java -jar
ENTRYPOINT ["java","-jar","/app/ezbilling-1.0.0.jar"]