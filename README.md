# E-WALLET 
___
### Spring Boot and ReactJS Application

---
This project provides to Implement e-wallet with REST API to create it, top it up, check its balance and withdraw funds..

### Summary
Our objective is to develop an e-wallet that enables users to conveniently store all their payment cards in one place. Additionally, our platform will empower users to perform financial transactions such as withdrawals, deposits, and fund transfers from within the e-wallet, using the money accounts they have added.

#### Requirements

• The API will expose an endpoint which accepts the account information (account, wallet,balance).

• Once the endpoint is called, a new wallet will be opened connected to the user whose account is accountId.

• Also, the balance will not fall below 0.

• Another Endpoint will output the user anathor cardinformation , showing Name, Surname, balance, andtransactions of the accounts.
___
The application has 5 apis
* AccountAPI
* WalletAPI
* MoneyBalanceAPI
* AnathorCardInformationAPI
* TransactionAPI

```html
POST /api/v1/accoun/create - creates a new account
POST /api/v1/wallet/create - creates a new wallet for existing account
POST /api/v1/moneybalance/create - creates a new moneybalance
POST /api/v1/anothercard/create - creates a new anathorcard
POST /api/v1/transaction/create - creates a new transaction

PUT /api/v1/moneybalance/update - update moneybalance
PUT /api/v1/transaction/update - transfer to transfer wallet

GET /api/v1/wallet/list - all wallet list
```
---
### JUnit test coverage is 70% as well as unit tests are available.


### Tech Stack

---
- Java 11
- Spring Boot
- Spring Data JPA
- Restful API
- Mysql 
- Docker
- Docker compose
- JUnit , Mockito
- Jasypt
- ReactJS for frontend

### Prerequisites

---
- Maven
- Docker

### Run & Build

---
There are 2 ways of run & build the application.

#### Docker Compose

You just need to run `docker-compose up` command
___
*$PORT: 8095*
```ssh
$ cd ewallet/ewallet-api
$ docker-compose up
```

#### Maven

For maven usage, you need to change `proxy` value in the `ewallet-fe/package.json` 
file by `"http://localhost:8095"` due to the default value has been settled for docker image network proxy.
___
*$PORT: 8095*
```ssh
$ cd ewallet/ewallet-api
$ mvn clean install
$ mvn spring-boot:run

$ cd ewallet/ewallet-fe
$ npm install
$ npm run start
```

#### Attention

A basic control authorization is used. Do not forget to add headers key in API requests.
```ssh
Authorization : "Basic dXNlcjpwYXNzd29yZA=="
```
