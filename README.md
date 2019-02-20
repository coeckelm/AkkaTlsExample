# Akka Http with Mutual TLS

This example is showing the implementation of sample Akka http server with mutual TLS on its endpoints
The routes are listening on the 
  - HTTP: localhost:8081
    - GET /helloworld
    - GET /hellofreeworld
  - HTTPS with client authentication: localhost:8443
    - GET /helloworld
    - GET /hellosecureworld


To test this run sbt in this project
In Sbt run `compile` to compile your source or run.
To simulate the Http calls you can use Postman.