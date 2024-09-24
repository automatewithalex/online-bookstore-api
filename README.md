# Online Bookstore API

This is an automated testing project for the **Online Bookstore API**, built using **Java**, **TestNG**, **RestAssured**, and **Allure** for reporting. The project includes API tests for managing books and authors, covering operations like creating, updating, retrieving, and deleting entities.

## Project Structure

## Prerequisites

- **Java 23**
- **Maven**
- **Allure**

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
    mvn clean test -Dsurefire.suiteXmlFiles=testng.xml

2. **After running the tests, generate the Allure report**:
    
    ```bash
   mvn allure:report

3. **You can view the report locally by serving it with Allure**:

    ```bash
   allure serve target/allure-results


## GitHub Actions and CI/CD

This project is integrated with **GitHub Actions** for continuous integration. On every push to the **master** branch:

- Tests are executed.
- An **Allure report** is generated and deployed to **GitHub Pages**.
- Artifacts (Allure results and reports) are saved.

## Viewing the Allure Report
The latest Allure report is available on GitHub Pages:

[View Allure Report](https://automatewithalex.github.io/online-bookstore-api)

## Key Features

- **TestNG**: Used for structuring tests.
- **RestAssured**: API testing framework for HTTP requests and responses.
- **Allure**: Detailed test reporting with visual insights.
- **Data-Driven Tests**: Utilizes data providers for parameterized testing.
- **CI/CD Integration**: Automated test runs and report generation on GitHub Actions.