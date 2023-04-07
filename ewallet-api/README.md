# E-WALLET

---
### Database Diagram
![DatabaseDiagram](/assets/dbdiagram.png)

### The service provides an API
#### `E-WALLET API`

* To see all to all wallet


## Technologies

---
- Java 11
- Spring Boot 
- Spring Data JPA
- MySQL
- Restful API
- Maven
- Junit
- Mockito
- Docker
- Docker Compose
- Github 
- Jasypt


## Prerequisites

---
- Maven or Docker
---

## Providing

---
- Postman <a href="/assets/ewalletApplication.postman_collection.json" download>collections</a> can be accessed under the Asset folder.
---

![postmanCollection](/assets/collection.png)


## Spring Boot Password Encryption Using Jasypt
---
- Stepts To Add Encryption Using Jasypt
- Add jasypt-spring-boot-starter maven dependency in the pom.xml
- Add System Property Spring Boot Application main class
- Select a secret key to be used for encryption and decryption
- Encyrpting all values in the application.properties file


```html
 <dependency>
            <groupId>com.github.ulisesbocchio</groupId>
            <artifactId>jasypt-spring-boot-starter</artifactId>
            <version>3.0.4</version>
 </dependency>
```
```html
  <dependency>
            <plugin>
                <groupId>com.github.ulisesbocchio</groupId>
                <artifactId>jasypt-maven-plugin</artifactId>
                <version>3.0.4</version>
  </plugin>
```

These should be in your properties file.

```html
jasypt.encryptor.algorithm= PBEWithMD5AndDES
jasypt.encryptor.iv-generator-classname: org.jasypt.iv.NoIvGenerator
```


Now wrap the values you need to be encrypted inside DEC() parenthesis as follows.

```html
spring.datasource.username= DEC(my-user-name)
spring.datasource.password= DEC(my-password)
```
Open the command prompt in the same directory and type

```html
mvn jasypt:encrypt -Djasypt.encryptor.password=my-secret-value
```

When you will run this command it will automatically replace the DEC() placeholder with ENC() and the encrypted text in between, as follow

```html
spring.datasource.username= ENC(lsmeYXruN4BKByLVPQ0Xzg==)
spring.datasource.password= ENC(K+13VBcpGkuJpoC4PQ/xhA==)
```


