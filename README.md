# Task Management System

A full-stack **Task Management System** web application built with **Java 17, Spring Boot 3, Maven, MySQL, Bean Validation, JUnit 5/Mockito**, and a responsive **HTML5/CSS3/JavaScript (Fetch API)** frontend.

This project is prepared to practice a full CI/CD pipeline with **Git, Maven, Jenkins, Docker, Docker Hub, and Kubernetes**.

---

## Table of Contents

- [Features](#features)
- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [MySQL Database Setup](#mysql-database-setup)
- [Environment Variables Configuration](#environment-variables-configuration)
- [Getting Started & Running Locally](#getting-started--running-locally)
- [Maven Commands](#maven-commands)
- [API Endpoints](#api-endpoints)
- [Accessing the Web Application](#accessing-the-web-application)
- [Docker Instructions](#docker-instructions)
- [CI/CD Readiness Roadmap](#cicd-readiness-roadmap)

---

## Features

- **Full Task Lifecycle**: Add, view, update, delete, mark as completed, and filter tasks by status (`TODO`, `IN_PROGRESS`, `COMPLETED`).
- **Modern Responsive Web UI**: Clean, custom HTML5/CSS3/JavaScript interface communicating seamlessly with the Spring Boot REST API using Fetch API (no React or Angular).
- **MySQL Integration**: Persistent storage with automatic schema management (`task_management`).
- **Bean Validation**: Strict payload validation (`@NotBlank`, `@NotNull`, Priority & Status Enums).
- **Global Error Handling**: `@ControllerAdvice` returning clean JSON error responses for `404 Not Found`, `400 Bad Request`, and `500 Internal Server Error`.
- **Automated Unit & Controller Testing**: Comprehensive test coverage with JUnit 5 & Mockito.
- **Port 8081**: Configured on port `8081` to avoid conflicts with Jenkins (running on port `8080`).

---

## Technologies Used

- **Backend**: Java 17, Spring Boot 3.2.5, Spring Web, Spring Data JPA, Hibernate, Bean Validation
- **Database**: MySQL 8.x (H2 used for automated test execution)
- **Frontend**: HTML5, CSS3, Vanilla JavaScript (Fetch API)
- **Build Tool**: Maven 3.x
- **Testing**: JUnit 5, Mockito, Spring Boot Test
- **Containerization**: Docker (Alpine Java 17 JRE)

---

## Project Structure

```text
task-management-system/
├── Dockerfile
├── .dockerignore
├── .gitignore
├── pom.xml
├── README.md
└── src
    ├── main
    │   ├── java
    │   │   └── com
    │   │       └── example
    │   │           └── taskmanagement
    │   │               ├── TaskManagementApplication.java
    │   │               ├── config
    │   │               │   └── DataInitializer.java
    │   │               ├── controller
    │   │               │   └── TaskController.java
    │   │               ├── exception
    │   │               │   ├── ErrorResponse.java
    │   │               │   ├── GlobalExceptionHandler.java
    │   │               │   └── ResourceNotFoundException.java
    │   │               ├── model
    │   │               │   ├── Priority.java
    │   │               │   ├── Status.java
    │   │               │   └── Task.java
    │   │               ├── repository
    │   │               │   └── TaskRepository.java
    │   │               └── service
    │   │                   └── TaskService.java
    │   └── resources
    │       ├── static
    │       │   ├── css
    │       │   │   └── style.css
    │       │   ├── js
    │       │   │   └── script.js
    │       │   └── index.html
    │       └── application.properties
    └── test
        ├── java
        │   └── com
        │       └── example
        │           └── taskmanagement
        │               ├── controller
        │               │   └── TaskControllerTest.java
        │               └── service
        │                   └── TaskServiceTest.java
        └── resources
            └── application-test.properties
```

---

## MySQL Database Setup

1. Make sure **MySQL Server** is installed and running on `localhost:3306`.
2. Create the database (or let Spring Boot create it automatically):
   ```sql
   CREATE DATABASE IF NOT EXISTS task_management;
   ```

---

## Environment Variables Configuration

The database username and password can be customized via environment variables:

- `DB_USERNAME` (default: `root`)
- `DB_PASSWORD` (default: `root`)

### Setting Environment Variables:

#### Linux / WSL / macOS:
```bash
export DB_USERNAME=myuser
export DB_PASSWORD=mypassword
```

#### Windows PowerShell:
```powershell
$env:DB_USERNAME="myuser"
$env:DB_PASSWORD="mypassword"
```

---

## Getting Started & Running Locally

Run the Spring Boot application using Maven:

```bash
mvn spring-boot:run
```

The application will start on **port 8081**.

---

## Maven Commands

- **Run Automated Tests**:
  ```bash
  mvn clean test
  ```

- **Package Production JAR**:
  ```bash
  mvn clean package
  ```

- **Run Packaged Application**:
  ```bash
  java -jar target/task-management-system-0.0.1-SNAPSHOT.jar
  ```

---

## API Endpoints

| Method | Endpoint | Description | Status Code |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/tasks` | Get all tasks | `200 OK` |
| `GET` | `/api/tasks/{id}` | Get task by ID | `200 OK` / `404 NOT FOUND` |
| `POST` | `/api/tasks` | Create a new task | `201 CREATED` / `400 BAD REQUEST` |
| `PUT` | `/api/tasks/{id}` | Update existing task | `200 OK` / `404 NOT FOUND` / `400 BAD REQUEST` |
| `DELETE` | `/api/tasks/{id}` | Delete a task | `204 NO CONTENT` / `404 NOT FOUND` |
| `PATCH` | `/api/tasks/{id}/complete` | Mark task as completed | `200 OK` / `404 NOT FOUND` |
| `GET` | `/api/tasks/status/{status}` | Filter tasks by status (`TODO`, `IN_PROGRESS`, `COMPLETED`) | `200 OK` |

---

## Accessing the Web Application

Open your browser and navigate to:

- 💻 **Task Management Dashboard**: [http://localhost:8081/](http://localhost:8081/)
- 🔌 **REST API Endpoint**: [http://localhost:8081/api/tasks](http://localhost:8081/api/tasks)

---

## Docker Instructions

### 1. Build Production Package
```bash
mvn clean package
```

### 2. Build Docker Image
```bash
docker build -t task-management-system .
```

### 3. Run Docker Container
```bash
docker run -d -p 8081:8081 --name task-app task-management-system
```

### 4. Stop and Remove Container
```bash
docker stop task-app
docker rm task-app
```

---

## CI/CD Readiness Roadmap

```text
GitHub Push ──> Jenkins Trigger ──> Maven Clean Test ──> Package JAR ──> Docker Build ──> Docker Hub Push ──> Kubernetes Deployment
```
