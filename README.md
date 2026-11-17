# Complaint Hub

**Complaint Hub** is a role-based complaint management web application designed to provide a centralized way to submit, manage, assign, track, and resolve complaints.

The application separates responsibilities between **Users**, **Agents**, and **Administrators**. Users can raise complaints and follow their progress, Administrators oversee complaint handling and assign work, and Agents work on complaints assigned to them.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Objectives](#objectives)
- [Key Features](#key-features)
- [User Roles](#user-roles)
- [Complaint Lifecycle](#complaint-lifecycle)
- [Technology Stack](#technology-stack)
- [System Architecture](#system-architecture)
- [Domain Model](#domain-model)
- [Security and Access Control](#security-and-access-control)
- [Project Structure](#project-structure)
- [Getting Started](#getting-started)
- [Configuration Notes](#configuration-notes)
- [Contributors](#contributors)
- [Future Enhancements](#future-enhancements)

---

## Project Overview

Complaint Hub aims to address the problems caused by unorganized complaint reporting, limited visibility into complaint progress, and unclear responsibility for resolving reported issues.

It provides a structured workflow in which complaints can be submitted, reviewed, assigned to agents, updated during handling, and resolved. The person who submitted a complaint retains the ability to confirm whether the proposed resolution actually solved the issue.

The project is developed with a **React frontend** and a **Java-based backend** using Servlets, Hibernate ORM, and Oracle Database.

## Objectives

- Provide a centralized platform for complaint submission and tracking.
- Establish clear responsibilities for users, administrators, and agents.
- Make complaint assignment and resolution easier to manage.
- Improve transparency through complaint updates and a defined lifecycle.
- Preserve the original complaint details while allowing follow-up information to be added.
- Enforce role-based permissions and protect complaint data.

## Key Features

### Complaint Management
- Submit complaints with relevant details and a category.
- View complaint information and follow its progress.
- Organize complaints using categories and priority.
- Keep submitted complaint details unchanged; additional information can be provided through complaint updates.

### Role-Based Workflows
- Separate access and responsibilities for Users, Agents, and Administrators.
- Administrators can review complaints and assign them to agents.
- Agents can work on complaints assigned to them.
- Users can review the resolution of their own complaints.

### Complaint Updates
- Add follow-up information to a complaint without editing its original submission.
- Support updates from authorized participants according to the project's access rules.
- Distinguish updates intended for internal handling from updates visible to users, where configured.

### Complaint Assignments
- Allow Administrators to assign complaints to Agents.
- Track assignment information and support active-assignment lookup.
- Restrict Agent complaint access and actions to their assigned work.

### Attachments
- Associate file attachments with complaints.
- Apply file validation for supported image types, extensions, and size limits.
- Store uploaded files using the configured upload location.

### User Profiles
- Support user profile information and profile-photo management.

### Authentication and Authorization
- Authenticate users through the application's session-based authentication flow.
- Hash and verify passwords using BCrypt.
- Protect API routes with an authentication filter.
- Apply role, ownership, and assignment checks before allowing protected operations.

---

## User Roles

| Role | Main Responsibilities |
|---|---|
| **USER** | Submit complaints, view their own complaints, add further information, and confirm or reopen a resolved complaint. |
| **AGENT** | View and handle assigned complaints, add relevant updates, and mark work as resolved. |
| **ADMIN** | Oversee complaint handling, review complaints, assign Agents, manage categories, and perform administrative actions such as changing complaint priority. |

Access is governed by role-based permissions and resource ownership/assignment checks.

## Complaint Lifecycle

The intended complaint workflow is:

```text
             Complaint Submitted
                      |
                      v
                    OPEN
                      |
             Admin assigns an Agent
                      |
                      v
                  ASSIGNED
                      |
             Assigned Agent starts work
                      |
                      v
                 IN_PROGRESS
                      |
             Agent resolves the issue
                      |
                      v
                  RESOLVED
                   /     \
                  /       \
       User confirms     User says the
        resolution       issue persists
              |               |
              v               v
            CLOSED        IN_PROGRESS
```

An Administrator may also reject a complaint during review when it is invalid, duplicated, insufficiently detailed, or outside the supported scope. A rejection should have an explanatory reason.

The application is designed around controlled status transitions rather than allowing arbitrary status changes.

## Technology Stack

| Area | Technology |
|---|---|
| Frontend | React |
| Backend | Java Servlets / JSP |
| ORM | Hibernate |
| Database | Oracle Database |
| Application Server | Apache Tomcat 11 |
| Build and Dependency Management | Apache Maven |
| Password Hashing | BCrypt |
| Version Control | Git and GitHub |

> **Version note:** The development environment documented for the backend uses Java 26, Maven, and Tomcat 11. Check the project's build configuration before choosing a different Java or server version.

## System Architecture

Complaint Hub follows a layered backend structure so that HTTP handling, business rules, and database operations remain separate.

```text
                  React Frontend
                        |
                        | HTTP Requests
                        v
              Java Servlet Controllers
                        |
                        v
              Authentication Filter
             (protected API requests)
                        |
                        v
                  Service Layer
              (business rules/workflows)
                        |
                        v
                    DAO Layer
               (database operations)
                        |
                        v
                 Hibernate ORM
                        |
                        v
                 Oracle Database
```

### Backend Layer Responsibilities

- **Controllers:** Receive HTTP requests, obtain request/session data, call services, and produce HTTP responses.
- **Filters and Utilities:** Handle shared concerns such as authentication, authorization support, validation, and file checks.
- **Services:** Enforce application rules and coordinate operations across DAOs.
- **DAOs:** Encapsulate persistence operations using Hibernate.
- **Entities and Enums:** Represent the domain model and controlled values used by the application.

## Domain Model

The backend domain currently includes the following principal entities:

- `User`
- `Category`
- `Complaint`
- `ComplaintAssignment`
- `ComplaintAttachment`
- `ComplaintUpdate`
- `UserProfilePhoto`

The domain also uses enums including:

- `UserRole`
- `ComplaintStatus`
- `ComplaintPriority`

These entities support user accounts, complaint categorization, complaint records, agent assignments, attachments, updates, and profile photos.

## Security and Access Control

Security-related backend components include:

- **AuthenticationFilter** – protects `/api/*` endpoints by checking the authenticated session.
- **AuthorizationUtil** – provides role and ownership authorization checks.
- **PasswordHasher** – handles BCrypt password hashing and verification.
- **ValidationUtil** – validates common input values such as IDs, required fields, lengths, and email addresses.
- **FileValidationUtil** – validates uploaded files, including image type, extension, and size.

The authenticated user's identity should be obtained from the server-side session rather than trusted from a client-submitted user ID. Authorization must also consider whether the user owns a complaint or is assigned to it.

---

## Project Structure

The exact directory layout may evolve as development continues. At a high level, the repository is organized around:

```text
Complaint-Hub/
├── frontend/                 # React application
├── backend/                  # Java backend (if maintained as a separate folder)
│   ├── src/
│   │   └── main/
│   │       ├── java/
│   │       │   └── com/complainthub/
│   │       │       ├── controller/
│   │       │       ├── dao/
│   │       │       ├── entity/
│   │       │       ├── service/
│   │       │       └── util/
│   │       └── resources/
│   └── pom.xml
├── docs/                     # Project and domain documentation
└── README.md
```

If the frontend and backend are stored in different repositories or use different folder names, adjust this illustrative tree to match the actual repository layout.

## Getting Started

The following is a general setup outline. Use the actual paths and configuration values from the repository and your local Oracle/Tomcat environment.

### Prerequisites

Install or prepare:

- Git
- Node.js and npm
- Java Development Kit compatible with the backend build
- Apache Maven
- Oracle Database
- Apache Tomcat 11
- An IDE such as IntelliJ IDEA (recommended for backend development)

### 1. Clone the Repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd Complaint-Hub
```

### 2. Set Up the Backend

1. Open the backend project in your IDE.
2. Configure the Oracle database connection used by the application.
3. Review the Hibernate configuration and entity registration.
4. Configure the complaint attachment upload directory.
5. Build the backend with Maven:

```bash
mvn clean package
```

6. Deploy the generated web application artifact to your configured Tomcat 11 server.
7. Start the server and verify that the application can connect to Oracle.

### 3. Set Up the Frontend

Open a terminal in the React frontend directory and run:

```bash
npm install
npm run dev
```

If the project uses a different start script, check the frontend `package.json` and use the script defined there.

### 4. Connect Frontend and Backend

Configure the frontend's API base URL to point to the backend server. Ensure that the backend CORS/session configuration and frontend request settings are appropriate for the local development environment.

### 5. Verify the Application

- Confirm that the backend starts successfully and connects to Oracle.
- Confirm that the frontend starts successfully.
- Test login and logout.
- Verify that role-specific pages and protected operations behave as expected.
- Test the complaint submission, assignment, update, attachment, and resolution workflows supported by the current build.

---

## Configuration Notes

Some settings depend on the local development environment and should not be committed with personal credentials.

- **Database:** Configure the Oracle JDBC connection details and credentials in the backend's supported configuration location.
- **Uploads:** The backend uses the `complainthub.upload.root` system property to determine the file upload root directory. Set it to a writable location on your machine/server.
- **Sessions:** Ensure the frontend and backend are configured to send session cookies correctly in the chosen development/deployment setup.
- **Secrets and credentials:** Do not commit database passwords, private keys, production credentials, or other secrets. Use local environment-specific configuration.

The exact property names and configuration files should be confirmed from the current backend source before deployment.

## Contributors

| Contributor | Role & Contribution |
|---|---|
| **Shashank Mandoli** | Backend Developer – Backend architecture, database design, Java Servlet and Hibernate implementation, REST API development, authentication, authorization, business logic, and frontend-backend integration. |
| **Tanu Mittal** | Frontend Developer – React frontend development, UI/UX design, component development, and frontend implementation. |

**Project Repository:** [Complaint Portal – GitHub](https://github.com/Tanu-Mittal/Complaint_Portal)
**Frontend Repository:** [Complaint Portal – GitHub](https://github.com/Tanu-Mittal/Complaint_Portal)

## Future Enhancements

Potential areas for future development include:

- Automated notifications for complaint assignment and status changes.
- More comprehensive audit/history views for complaint activity.
- Additional attachment formats and media support, subject to validation and storage requirements.
- Improved reporting and analytics for complaint trends and resolution performance.
- Further API documentation and automated backend/frontend testing.

These are possible future enhancements and should not be interpreted as features already available in the current release.

---

## Project Status

Complaint Hub is being developed as an academic project. Features and implementation details may evolve as frontend-backend integration, testing, and refinement continue.

---

**Built collaboratively by Shashank Mandoli (Backend) and Tanu Mittal (Frontend).**
