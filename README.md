

# BUILD:
  `./gradlew build`

# DATABASE CONF (application.properties)
  spring.datasource.url=jdbc:postgresql://localhost:5432/gdpr<br/>
  spring.datasource.username=postgres<br/>
  spring.datasource.password=postgres<br/>
  spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect<br/>
  spring.jpa.generate-ddl=true

# RUN:
  `java -Dspring.profiles.active=production -Dserver.port=8080 -jar /build/libs/gdpr-0.0.1-SNAPSHOT.jar`
