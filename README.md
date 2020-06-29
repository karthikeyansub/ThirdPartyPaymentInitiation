# ThirdPartyPaymentInitiation

#Create folders to generate all files (separated for client and server)
mkdir ssl && cd ssl && mkdir client && mkdir server
## Server
create new file server/serverPrivateKey.pem using the Private Key from the Assignments -Senior/Assignments/example-signature.md file
create new file server/server.crt using the Signature Certificate given in the Assignments -Senior/Assignments/example-signature.md file

## Client
# Generate client's private key and a certificate signing request (CSR)
> openssl req -new -newkey rsa:4096 -out client/request.csr -keyout client/myPrivateKey.pem -nodes
## Server
# Sign client's CSR with server private key and a related certificate
> openssl x509 -req -days 360 -in request.csr -CA server/server.crt -CAkey server/serverPrivateKey.pem -CAcreateserial -out client/cilentCertificate.crt -sha256
## Client
# Create PKCS12 keystore containing client's private key and related self-sign certificate 
> openssl pkcs12 -export -out client/clientKeyStore.p12 -inkey client/myPrivateKey.pem -in client/cilentCertificate.crt -certfile server/server.crt

## Run Spring Boot Application

# Build the JAR using maven from pom.xml file location

> mvn clean package

# Start the Spring Boot application

> java -jar /target/payment-initiation-0.0.1-SNAPSHOT.jar

## Test the application using Postman Rest Client

Step #1: Add the client certificate in Postman -> Settings -> Certificate tab

Step #2: invoke the initiate-payment service with below URL

> https://localhost:8080/v1.0.0/initiate-payment
