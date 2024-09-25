# Online Bookstore API

This project is an automated test suite designed for validating the **Online Bookstore API**. It is built using **Java**, **Maven**, **TestNG**, and **RestAssured**, with **Allure** providing comprehensive test reporting. The framework ensures thorough testing of core API functionalities, including creating, updating, retrieving, and deleting books and authors.

## Key Features

- **Comprehensive API Testing**: Includes test cases for all major CRUD operations (Create, Read, Update, Delete) on both books and authors.
- **Modular Structure**: Well-organized project structure separating configuration, test data, utilities, and test cases for scalability and maintainability.
- **Data-Driven Testing**: Supports data-driven tests using external JSON files, allowing easy expansion of test cases without modifying code.
- **Environment-Specific Configuration**: Easily configurable to run in different environments (development, staging, production) through external property files.
- **Logging**: Integrated logging for easy debugging and tracking of test execution.
- **Detailed Reporting**: Leverages Allure for clean, visual, and comprehensive reports, making it easy to track the status of each test case and debug failures.
  
## Tech Stack:
- **Programming Language**: Java
- **Build Tool**: Maven
- **Test Framework**: TestNG
- **API Testing**: RestAssured
- **Reporting**: Allure
- **Version Control**: Git

## Prerequisites

For windows:
- [Git](https://git-scm.com/downloads/win)
- [Java 23 JDK](https://www.oracle.com/java/technologies/downloads/#jdk23-windows)
- [Maven Binaries](https://maven.apache.org/download.cgi)
- [Allure](https://allurereport.org/docs/install-for-windows/)

For linux:
- [Git](https://git-scm.com/downloads/linux)
- [Java 23 JDK](https://www.oracle.com/java/technologies/downloads/#jdk23-linux)
- [Maven Binaries](https://maven.apache.org/download.cgi)
- [Allure](https://allurereport.org/docs/install-for-linux/)

## Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/automatewithalex/online-bookstore-api.git

2. **Navigate to the project directory**:

    ```bash
    cd online-bookstore-api

3. **Install dependencies**:

    ```bash
    mvn clean install

## Running Tests

1. **To run the API tests with Maven**:

    ```bash
    mvn clean test -Dsurefire.suiteXmlFiles=testng.xml -Dlog.level=info

2. **After running the tests, generate the Allure report**:
    
    ```bash
   mvn allure:report

3. **You can view the report locally by serving it with Allure**:

    ```bash
   allure serve target/allure-results

## Framework Structure:
- **Config**: Centralized configuration management with environment-specific properties.
- **Models**: POJOs representing request and response bodies for books and authors.
- **Data Providers**: JSON-based data providers to drive parameterized tests.
- **Tests**: API test classes for authors and books, structured to ensure clarity and maintainability.
- **Utils**: Helper classes for common functions such as logging, JSON parsing, and assertions.

## GitHub Actions and CI/CD

This project is integrated with **GitHub Actions** for continuous integration. On every push to the **master** branch:

- Tests are executed.
- An **Allure report** is generated and deployed to **GitHub Pages**.
- Artifacts (Allure results and reports) are saved.

## Viewing the Allure Report
The latest Allure report is available on GitHub Pages: [View Allure Report](https://automatewithalex.github.io/online-bookstore-api)
