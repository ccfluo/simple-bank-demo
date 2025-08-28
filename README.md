
# Simplebank project

This is the demo banking backend application, built using Spring Boot. It provides API for managing customers, bank accounts, and account operations. 

## Version
| Version | Date       |                                                                                                  |                                                                        
|---------|------------|--------------------------------------------------------------------------------------------------|
| 0.1     | 2025/08/18 | Demo version to test different framework                                                         |
| 0.2     | 2025/08/19 | Trigger SMS/Email notification(pseudocode）via Kafka messaging for deposit/withdrawal transaction |
| 0.3     | 2025/08/21 | Enhance Global Exceptional handler                                                               |
| 0.4     | 2025/08/24 | Add transaction summary report at 2am daily                                                      |
| 0.5     | 2025/08/28 | **New functions:** <br> - Wealth product purchase <br> - Money Transfer                          |
|         |            |                                                                                                  |
|         |            |                                                                                                  |


## Features

### Customer Management:
- Create, update, and delete customers.
- Retrieve customer details and associated bank accounts.

### Bank Account Management:
- Create new accounts.
- Retrieve account details and transaction history.

### Transaction Management:
- deposit/withdraw money from account: send SMS/Email notification
- View account transactions history.
- transfer account: send SMS/Email notification for transfer

### Wealth Product Purchase:
- Purchase wealth product: send SMS/Email notification for transfer

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
- **GET /customers/{customerId}**: Retrieve details of a specific customer by ID.
- **POST /customer/create**: Create a new customer.
- **POST /customer/update**: Update an existing customer.
- **DELETE /customer/{customerId}**: Delete a customer by ID.

### Bank Account Management
- **GET /accounts/{accountId}**: Retrieve details of a specific bank account by ID.
- **GET /accounts/all**: Retrieve a list of all bank accounts.
- **GET /account/bycustomer?id={customerId}**: Retrieve all bank accounts for a specific customer.
- **POST /accounts/debit**: Perform a debit operation on an account.
- **POST /accounts/credit**: Perform a credit operation on an account.

### Transaction Management
- **POST /transaction/withdraw**: Withdraw money from a banking account.
- **POST /transaction/deposit**: Deposit money to a banking account.
- **GET /transaction/history/{accountId}** Retrieve the transaction history for a specific account.
- **POST /transaction/transfer** transfer money to other account

### Wealth Management
- **POST /product/purchase**: Purchase a wealth product.

### Exception Handling
The application uses global exception handling to handle specific scenarios
    
