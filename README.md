# Getting Started

 1 - Compiling and Running with Maven:  mvn clean install
 2 -  Running the application: mvn spring-boot:run

there ara two users for Basic Authantication:

-   user : admin password : 12345 role: USER,ADMIN

-   user : fuat  password : 54321 role : USER

## Works on localhost 8080 

you can try this request :

[POST] localhost:8080/orders/create
```json
{
"customerId": "2",
"assetName": "AKSA",
"orderSide": "SELL",
"size": 2,
"price": 6.0
}
```

[POST] localhost:8080/orders/create
```json
{
"customerId": "1",
"assetName": "AKSA",
"orderSide": "BUY",
"size": 5,
"price": 6.0
}
```
[GET] [localhost:8080/orders/listAll](http://localhost:8080/orders/listAll "localhost:8080/orders/listAll")

[GET] [localhost:8080/assets/list?customerId=2](http://localhost:8080/orders/listAll "localhost:8080/assets/list?customerId=2")

[GET] [localhost:8080/orders/match](http://localhost:8080/orders/match "localhost:8080/orders/match")  // only ADMIN role can execute

[DELETE] [localhost:8080/orders/cancel/3](http://localhost:8080/orders/cancel/3 "localhost:8080/orders/cancel/3")

# Working structure
Users can place buy and sell orders for certain assets at the price they want. If there is a sell order in the order 
pool for the same price, this is executed and recorded in the assets table. When creating orders, a PENDING status is 
created. If the order is executed for the relevant quantity, the status becomes MATCHED. Data for the relevant customers
is updated in the ASSETS table. If an order with a PENDING status is canceled by the customer, the status of the 
relevant order becomes CANCELED. Data in the Asset table is updated.
