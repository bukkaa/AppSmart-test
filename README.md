# AppSmart-test

Test application providing Customers and Products Endpoints.
`GET` and `POST` methods are allowed for anyone, and `PUT` and `DELETE` - for authenticated users only.

### Security

No roles or user credentials applied, the only thing needed for authentication is Token.
To acquire the token user need to call TokenController and pass the username (it'll be used as Principal further):

`GET .../api/v1/token?username=<...>`

Token `expiration` and `secret` could be setup in the _application.yml_.

The token is to be passed then in `AUTHORIZATION` header along with each request to secured endpoints.
Custom filter has been inserted into standard Spring Boot security Filter Chain to handle received tokens. 

### DB layer

Application uses Spring Data JPA to work with PostgreSQL and creates _Customers_ and _Products_ tables automatically. No initial data inserted.
DB connection URL and credentials could be setup in the _application.yml_.  

### Tech stack

Application also uses Maven, Java 11, Spring Boot 2.6.2, MapStruct 1.4.2.FINAL, 
and JJWT 0.9.1 and Lombok for more convenient work with tokens and reduction of boilerplate code.
