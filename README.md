
# Simplebank project

This is the demo banking backend application, built using Spring Boot. It provides API for managing customers, bank accounts, and account operations. 

## Version
| Version | Date       |                                                                                                                                                                                        |                                                                        
|---------|------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 0.1     | 2025/08/18 | Demo version to test different framework                                                                                                                                               |
| 0.2     | 2025/08/19 | Trigger SMS/Email notification(pseudocode）via Kafka messaging for deposit/withdrawal transaction                                                                                       |
| 0.3     | 2025/08/21 | Enhance Global Exceptional handler                                                                                                                                                     |
| 0.4     | 2025/08/24 | **New function:** <br> - generate transaction summary report at 2am daily                                                                                                              |
| 0.5     | 2025/08/28 | **New function:** <br> - Wealth product purchase <br> - Money Transfer                                                                                                                 |
| 0.6     | 2025/08/30 | **New function:** <br> - Warm up hot products' information to redis <br> **Function Enhancement:** <br> - Enable support for high concurrency in hot wealth product purchase scenarios |
| 0.7     | 2025/09/05 | **Function Enhancement:** <br> - Support batch or real time SMS/Email notification                                                                                                     |
 0.8      | 2025/09/08 | **New infra Function API:** <br> - Redis to support ranking/like                                                                                                                       |


## Features

### Customer Management:
- Create, update, and delete customer.
- Retrieve customer details and associated bank accounts.

### Bank Account Management:
- Create new accounts.
- Retrieve account details and transaction history.

### Transaction Management:
- deposit/withdraw money from account: send an SMS/Email notification.
- View account transactions history.
- transfer account: send SMS/Email notification for transfer

### Wealth Product Purchase:
- Purchase wealth product: send an SMS/Email notification for transfer.
- Retrieve wealth product details.
- Preload hot products' information into redis.

## Technologies Used
- **Spring Boot**: Framework for building the backend application.
- **Mybatis**: For database interactions and ORM (Object-Relational Mapping).
- **MySQL**: For database storage.
- **Redis**: In-memory data store for high-speed caching of data, enabling fast access and reducing database load.
- **Lombok**: For reducing boilerplate code with annotations like `@Data`, `@Getter`, and `@Setter.
- **Sentinel**: For flow control and service governance.It to safeguard critical services, manage traffic spikes, and enhance fault tolerance.
- **RESTful API**: Exposes endpoints for CRUD operations and banking transactions.
- **Kafka**: Distributed messaging system for asynchronous communication, decoupled services, and reliable real-time data streaming.
- **Quartz**: Distributed job scheduling framework used for executing tasks on a schedule (e.g., report generation, data cleanup, periodic synchronization).
- **Redission**: Redis client that provides distributed locks, distributed collections, and other concurrency utilities.


## Installation

### Prerequisites
- **Java 17 or higher**

### Run the Application:

The application will start on `http://localhost:8886`.


## API Endpoints
#### api prefix /admin is configured for all controllers

### Customer Management
- **GET /customer/all**: Retrieve a list of all customers.
- **GET /customer/{customerId}**: Retrieve details of a specific customer by ID.
- **POST /customer/create**: Open a new customer.
- **POST /customer/update**: Update an existing customer.
- **DELETE /customer/{customerId}**: Delete a customer.

### Bank Account Management
- **GET /account/{accountId}**: Retrieve details of a specific bank account by ID.
- **GET /account/all**: Retrieve a list of all bank accounts.
- **GET /account/bycustomer?id={customerId}**: Retrieve all bank accounts for a given customer.
- **POST /account/create**: Open an account.
- **POST /account/update**: Update an existing account.
- **DELETE /customer/{customerId}**: Delete an account.

### Transaction Management
- **POST /transaction/withdraw**: Withdraw money from a banking account.
- **POST /transaction/deposit**: Deposit money to a banking account.
- **GET /transaction/history/customer?id={customerId}**: Retrieve the transaction history for a given customer.
- **GET /transaction/history/account?id={accountId}**: Retrieve the transaction history for a given account.
- **POST /transaction/transfer**: Transfer money to other account

### Wealth Management
- **POST /product/purchase**: Purchase a wealth product.
- **GET /product/{productId}**: Retrieve details of a specific product.
- **GET /product/on-sale**: List all wealth products on Sale.
- **GET /product/purchase/history/{customerId}**: Retrieve all purchase history for a given customer.

### Ranking
- **POST /api/ranking/score**: add/update ranking.
- **POST /api/ranking/score/increment**: increase score
- **GET /api/ranking/score/{clientId}**: inquire client score
- **GET /api/ranking/rank/{clientId}**: inquire client rank
- **GET /api/ranking/top/{topN}**: get topN rank clients
- **GET /api/ranking/around/{clientId}**: get other rank around clients
- **GET /api/ranking/total**： get total client ranked
- 
### Like
- **POST /api/likes/{contentId}/like?userId={userId}**: like a contendId.
- **POST /api/likes/{contentId}/cancel?userId={userId}**: cancel a like
- **GET /api/likes/{contentId}/count**: inquire like count
- **GET /api/likes/{contentId}/check?userId={userId}**: check if user liked contentId
- **GET /api/likes/{contentId}/users**: get liked users
- **GET /api/likes/rank?topN=10**: get topN liked


## Exception Handling
The application uses global exception handling to handle specific scenarios

## Regular Jobs
- **Daily transaction report**: generated @2am daily
- **Hot Product quota synchronization**: sync from redis to DB every 5 minutes from 8am ~ 5pm + @2am daily
- **Hot Product batch warm up**: load hot product quota into redis after quota synchronization @2am daily
