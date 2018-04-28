# BUILD:
./gradlew build

# RUN:
java -Dspring.profiles.active=production -Dserver.port=8080 -jar /build/libs/gdpr-0.0.1-SNAPSHOT.jar
