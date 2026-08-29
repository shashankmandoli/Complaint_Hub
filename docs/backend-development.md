# Complaint-Hub Backend Development

This document records the development journey of the Complaint-Hub backend.

The goal is not only to build the application but also to document the technical decisions, concepts learned, problems encountered, and development workflow followed during the project.

---

# Project Overview

## Project Name

Complaint-Hub

## Project Description

Complaint-Hub is an application designed to track, monitor, manage, and resolve complaints.

The application will provide functionality for different users involved in the complaint resolution process.

The primary focus of the project is currently on the backend development and the main functionalities required by administrators and agents.

---

# Technology Stack

## Frontend

- React

The frontend was already developed before the backend implementation.

The frontend and backend will communicate through HTTP-based APIs.

## Backend

- Java 26
- Jakarta Servlets
- JSP
- Hibernate
- Maven
- Apache Tomcat 11

## Database

- Oracle Database

## Development Tools

- IntelliJ IDEA Ultimate
- Git
- GitHub

---

# Development Workflow

The project follows a feature-based development workflow.

Each meaningful feature is developed using the following process:

```text
Create Feature Branch
        ↓
Understand Requirement
        ↓
Design Solution
        ↓
Implement Feature
        ↓
Run Application
        ↓
Test Feature
        ↓
Review Changes
        ↓
Update Documentation
        ↓
Commit Changes
        ↓
Merge into Main
```

Git is used throughout the development process instead of only being used as a backup mechanism.

---

# Development Journal

---

## Phase 1: Backend Bootstrap

### Objective

The objective of the first phase was to create the initial Java backend infrastructure and successfully deploy a Servlet application.

The first milestone was to create and test a simple HTTP endpoint.

---

## Initial Project Structure

The project was organized as a single repository containing both the frontend and backend.

```text
Complaint-Hub/
│
├── frontend/
│
├── backend/
│
├── docs/
│
├── .gitignore
└── README.md
```

The project uses one Git repository at the root level.

```text
Complaint-Hub/.git
```

The frontend and backend do not have separate Git repositories.

---

## Git Initialization

Git was initialized at the root of the project.

```bash
git init
```

The primary branch was configured as:

```text
main
```

A root-level `.gitignore` file was added to prevent generated files and IDE configuration files from being committed.

Examples of ignored files include:

```text
.idea/
*.iml
target/
node_modules/
out/
.env
```

The initial project structure was committed before backend development began.

---

## Feature Branch

The backend bootstrap work was developed on a dedicated feature branch.

```text
feature/backend-bootstrap
```

The purpose of using a feature branch was to keep the backend setup isolated from the stable `main` branch.

The workflow followed was:

```text
main
 │
 └── feature/backend-bootstrap
```

All backend bootstrap changes were developed and tested on this branch before merging into `main`.

---

# Maven Backend Setup

A Maven project was created inside the backend directory.

The backend project structure was initialized as:

```text
backend/
│
├── pom.xml
│
└── src/
    └── main/
        └── java/
```

The Maven project was configured with the following identity:

```text
Group ID:
com.complainthub

Artifact ID:
complaint-hub-backend
```

---

# Java Version

The project uses:

```text
Java 26
```

The Maven compiler configuration uses:

```xml
<maven.compiler.release>26</maven.compiler.release>
```

The `release` property is used to define the Java version used for compilation.

---

# WAR Packaging

The backend is configured as a web application.

Therefore, the Maven packaging type was changed to:

```xml
<packaging>war</packaging>
```

A WAR file is a web application archive.

The deployment flow is:

```text
Java Source Code
        ↓
Maven Build
        ↓
WAR Application
        ↓
Apache Tomcat
        ↓
Running Web Application
```

During development, IntelliJ deploys the application using an exploded WAR artifact.

---

# Servlet API

The backend uses Jakarta Servlets for handling HTTP requests.

The Jakarta Servlet API dependency was added to the Maven project.

```text
jakarta.servlet
```

The dependency scope is configured as:

```xml
<scope>provided</scope>
```

This means that the Servlet API is required during compilation, but the Servlet container provides the runtime implementation.

In this project, Apache Tomcat acts as the Servlet container.

---

# Apache Tomcat Configuration

Apache Tomcat 11 was installed and configured as the local application server.

Tomcat is responsible for:

- Receiving HTTP requests
- Managing the Servlet lifecycle
- Mapping URLs to Servlets
- Sending HTTP responses

The backend application was deployed to:

```text
Apache Tomcat 11
```

The local server runs on:

```text
http://localhost:8080
```

---

# First Backend Endpoint

The first endpoint created for the project is:

```text
GET /api/hello
```

The endpoint is implemented using:

```text
HelloServlet
```

The Servlet is mapped using the `@WebServlet` annotation.

Conceptually:

```text
GET /api/hello
        ↓
@WebServlet("/api/hello")
        ↓
HelloServlet
        ↓
doGet()
        ↓
JSON Response
```

The endpoint returns:

```json
{
  "message": "Hello from Complaint-Hub backend"
}
```

---

# Request Lifecycle

The first successful request demonstrated the basic Servlet request lifecycle.

```text
Client
   │
   │ HTTP GET Request
   ▼
Apache Tomcat
   │
   │ URL Mapping
   ▼
HelloServlet
   │
   │ doGet()
   ▼
HttpServletResponse
   │
   ▼
JSON Response
```

When the client requests:

```text
http://localhost:8080/api/hello
```

Tomcat identifies the Servlet mapped to the URL.

The request is then handled by the `doGet()` method.

The Servlet writes a JSON response to the HTTP response body.

---

# Servlet Mapping Approach

The project currently uses annotation-based Servlet mapping.

Example:

```java
@WebServlet("/api/hello")
```

This approach was chosen instead of XML-based Servlet configuration.

Therefore, a `web.xml` file is not required for the current Servlet implementation.

---

# Problems Encountered

## Incorrect web.xml Structure

During the initial backend setup, unnecessary `web.xml` files were accidentally created in incorrect locations.

The created paths included:

```text
backend/src/main/web/WEB-INF/web.xml

backend/src/main/web/web/WEB-INF/web.xml
```

These files were not required because the project uses annotation-based Servlet mapping.

The files were removed before the backend bootstrap feature was finalized.

---

## IntelliJ Compiled Output

An IntelliJ-generated output directory appeared as an untracked file:

```text
out/
```

This directory contains generated compilation output and should not be committed.

The directory was added to `.gitignore`.

---

# Git Review Workflow

Before committing the backend feature, Git commands were used to inspect the changes.

The workflow was:

```bash
git status
```

Used to inspect modified, staged, and untracked files.

```bash
git diff
```

Used to inspect changes that had not yet been staged.

```bash
git add
```

Used to move selected changes into the staging area.

```bash
git diff --cached
```

Used to inspect exactly what would be included in the next commit.

This workflow helped identify unnecessary files before they were committed.

---

# Backend Bootstrap Result

The backend bootstrap phase was successfully completed.

The application can now:

- Compile as a Maven project
- Use Java 26
- Use Jakarta Servlets
- Run inside Apache Tomcat 11
- Receive HTTP requests
- Route requests to Servlets
- Return JSON responses

The first working endpoint is:

```text
GET /api/hello
```

The endpoint was successfully tested at:

```text
http://localhost:8080/api/hello
```

---

# Concepts Learned

During this phase, the following concepts were introduced:

- Git repository initialization
- Feature branches
- Git staging area
- Git diff review
- Maven project configuration
- WAR packaging
- Jakarta Servlets
- Servlet containers
- Apache Tomcat
- HTTP GET requests
- URL mapping
- `@WebServlet`
- `HttpServlet`
- `HttpServletRequest`
- `HttpServletResponse`
- JSON responses
- Local application deployment

---

# Current Backend Status

Current status:

```text
Backend Bootstrap
        │
        ├── Maven Setup               ✓
        ├── Java 26 Configuration     ✓
        ├── Servlet API               ✓
        ├── Apache Tomcat             ✓
        ├── First Servlet             ✓
        ├── First API Endpoint        ✓
        └── Git Feature Workflow      ✓
```

The backend is now ready for the next development phase.

---

# Next Phase

The next phase will focus on understanding and designing the actual Complaint-Hub backend domain.

The following areas need to be designed before implementing the database:

- User roles
- Complaint lifecycle
- Complaint categories
- Complaint priorities
- Complaint statuses
- Admin responsibilities
- Agent responsibilities
- Complaint assignment
- Backend API structure
- Database entities and relationships

The domain design will be completed before introducing Hibernate and Oracle Database integration.