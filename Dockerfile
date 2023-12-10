FROM openjdk:17-oracle

COPY ./build/libs/task-management-system-api-0.0.1-SNAPSHOT.jar app.jar

COPY src/main/resources/data/ /root/data/

CMD ["java", "-jar", "app.jar"]