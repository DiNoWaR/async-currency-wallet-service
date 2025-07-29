## **Introduction**

The **Async Wallet Service** application is a REST API designed micro‑service that stores user balances in USD and TRY,
accepts deposit, withdrawal, and (optionally) currency‑exchange requests, publishes those requests to Kafka for
asynchronous processing, updates the database through a Kafka consumer, fetches live USD↔TRY rates with Redis caching,
and provides endpoints to check balances and the status of recent operations.
This project is implemented using **Spring Boot**, **Postgres**, **Kafka** and **Redis**, including **unit testing**,
and **containerization with Docker**.

## **How to Use**

### **Running the Application with Docker Compose**

1. Ensure you have **Docker** installed on your system.
2. Navigate to the `dev` folder in the project directory.
3. Run the following command to build the application, run tests, and start the services:
   ```sh
   docker compose up
   ```

This command will:

- Build the application using **Gradle**.
- Run unit tests.
- Start the application along with **Redis** and **Postgres** and **Kafka**.

1. Once the application is running, you can access the API using tools like **Postman** or **cURL**.

🔹 **Logging user**
- Request
```
curl -X POST http://localhost:8081/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe"
}'
```
- Response 
```
{
  "userId": "abc123",
  "username": "johndoe",
  "access_token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
}
```

🔹 **Deposit**
- Request
```
curl -X POST http://localhost:8081/transactions/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "X-Idempotency-Key: unique-key-123" \
  -d '{
    "amount": 100.00,
    "currency": "usd"
}'
```
- Response
```
{
  "trxId": "trx456",
  "operation": "DEPOSIT",
  "amount": 100.00,
  "status": "PENDING",
  "currency": "usd",
  "ts": "2025-07-25T06:15:42.192Z"
}
```


🔹 **Withdraw**
- Request
```
curl -X POST http://localhost:8081/transactions/withdraw \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your_jwt_token>" \
  -H "X-Idempotency-Key: unique-key-123" \
  -d '{
    "amount": 100.00,
    "currency": "usd"
}'
```
- Response
```
{
  "trxId": "trx456",
  "operation": "Withdraw",
  "amount": 100.00,
  "status": "PENDING",
  "currency": "usd",
  "ts": "2025-07-25T06:15:42.192Z"
}
```

🔹 **Balance**
- Request
```
curl -X GET http://localhost:8081/transactions/balance/user1 
```
- Response
```
{
  "userId": "user1",
  "balances" : [
   { 
    "currency": USD,
    "amount": 100
   },
   { 
    "currency": TRY,
    "amount": 10
   }
  ]
}
```

🔹 **Exchange Rate**

- Request
```
curl -X GET http://localhost:8081/transactions/exchange \
  -H "Content-Type: application/json" \
  -d '{
    "currency_from": "USD",
    "currency_to": "TRY"
}'
```
- Response
```
{
  "currency_from": "USD",
  "currency_to": "TRY",
  "rate": 0.85
}
```

🔹 **Transaction Status**
```
curl -X GET http://localhost:8081/transactions/status/1234
```
- Response

```
{
  "trxId": "abc123",
  "operation": "DEPOSIT",
  "amount": 100.00,
  "status": "PENDING",
  "currency": "usd",
  "ts": "2025-07-25T08:45:30Z"
}
```