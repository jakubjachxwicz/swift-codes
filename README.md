# Application Setup and Requirements

The application was built using the following technologies:
- **Java**, **Maven**, **Spring Boot**, **JPA**, **MySQL**
- **IDE**: IntelliJ IDEA, **OS**: Windows 11

<br>

## Database Configuration

The MySQL database is set up locally on port `3306`. Please ensure you create a schema named `swift_codes`. JPA will automatically handle the table creation and structure.

File `application.properties` already includes the following connection URL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/swift_codes?useSSL=false&serverTimezone=UTC
```

Make sure to create `secret.properties` file in the same directory. In it include the username and password for the
connection. It should look like this:
```properties
spring.datasource.username=***
spring.datasource.password=***
```
<br>

## Setting up, running and testing

Application was developed in IntelliJ IDE. If you open this project in it, make sure to go to the `pom.xml` file, right
click anywhere, and use:

`Maven > Sync Project`

To install all the necessary dependencies. Then to start the project you can use run `SwiftCodesApplicaion` file.
To perform test, go to the test directory, choose one, right click on it and press `Run`.

<br>
To start the project from command line, make sure that you have <b>Maven</b> installed:

`mvn -v`

Go to the project's main directory, run command: `mvn clean install`, to install necessary dependencies.
<br>
To run the app use command: `mvn spring-boot:run`
<br>
To run tests use command: `mvn test`

<br>

## Parsing data

To parse SWIFT codes data, run POST request to the dedicated endpoint:
<br>
`localhost:8080/admin/parse-data?filename=swiftcodes.csv`

File `swiftcodes.csv` is already included in project's repository, so all you have to do is run the request. After that,
data should be loaded and other endpoints can be used.