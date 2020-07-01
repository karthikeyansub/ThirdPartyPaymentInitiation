# ThirdPartyPaymentInitiation

# Create certificate file using the and private key file with the given value in the example-signature.md file

> certificate.crt
> privateKey.pem

## Run Spring Boot Application

# Build the JAR using maven from pom.xml file location

> mvn clean package

# Start the Spring Boot application

> java -jar /target/payment-initiation-0.0.1-SNAPSHOT.jar

## Test the application using Postman Rest Client

Step #1: Add the client certificate and private key file in Postman -> Settings -> Certificate tab

Step #2: invoke the initiate-payment service with below URL

> https://localhost:8080/v1.0.0/initiate-payment

Step #3: Use the payload, x-request-id, signature and signature-certificate given in the example-signature.md file for testing
