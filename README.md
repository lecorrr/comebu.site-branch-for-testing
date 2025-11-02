Port: 8090

DB config:
spring.datasource.url=jdbc:mysql://localhost:3306/demo_db?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=demo_user
spring.datasource.password=demo_pass
spring.jpa.hibernate.ddl-auto=update

Run:
mvn clean spring-boot:run

Open pages (matching your uploaded frontend file names):
http://localhost:8090/login.html
http://localhost:8090/registration.html
http://localhost:8090/shop.html
