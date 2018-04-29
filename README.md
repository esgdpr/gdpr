

# BUILD:
  ./gradlew build

# DATABASE CONF:
## In application.properties
  spring.datasource.url=jdbc:postgresql://localhost:5432/gdpr
  spring.datasource.username=postgres
  spring.datasource.password=postgres
  spring.jpa.generate-ddl=true

# RUN:
  java -Dspring.profiles.active=production -Dserver.port=8080 -jar /build/libs/gdpr-0.0.1-SNAPSHOT.jar
