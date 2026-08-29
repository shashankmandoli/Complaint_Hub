# Complaint-Hub Domain Design

This document defines the initial domain model and business concepts for the Complaint-Hub application.

The purpose of this document is to understand what the system represents before designing the database, Hibernate entities, APIs, and backend implementation.

This document focuses on the business domain rather than technical implementation details.

---

# 1. Project Overview

## Project Name

Complaint-Hub

## Description

Complaint-Hub is an application designed to track, monitor, manage, and resolve complaints.

The system allows users to submit complaints and monitor their progress.

Administrators manage the overall complaint system and assign complaints to agents.

Agents are responsible for investigating and resolving assigned complaints.

---

# 2. Domain Design Objective

Before creating database tables or Java entities, the business domain needs to be understood.

The development flow follows:

```text
Business Problem
        ↓
Identify Actors
        ↓
Define Responsibilities
        ↓
Identify Core Domain Objects
        ↓
Define Business Rules
        ↓
Define Entity Relationships
        ↓
Design Database
        ↓
Implement Backend
```

The goal is to avoid designing the database before understanding the actual requirements of the application.

---

# 3. System Actors

The initial version of Complaint-Hub contains three primary user roles.

```text
Complaint-Hub
│
├── User
├── Agent
└── Admin
```

All actors represent users of the system.

Instead of maintaining separate systems for each type of user, the application will use a common user model with role-based permissions.

---

# 4. User

A User represents a person who creates and tracks complaints.

## Responsibilities

A user can:

- Register and log in
- Create a complaint
- Select a complaint category
- Select or receive a complaint priority
- View their own complaints
- View complaint details
- Track complaint status
- View updates related to their complaints

## Example

A user creates a complaint:

```text
Title:
Internet connection unavailable

Description:
My internet connection has not been working for two days.

Category:
Technical

Priority:
High

Status:
Open
```

The user can later monitor the complaint until it is resolved.

---

# 5. Agent

An Agent is responsible for handling complaints assigned to them.

## Responsibilities

An agent can:

- Log in
- View assigned complaints
- View complaint details
- Start working on complaints
- Update complaint status
- Add progress updates
- Add resolution details
- Mark complaints as resolved

## Example Workflow

```text
Complaint Assigned
        ↓
Agent Reviews Complaint
        ↓
Agent Starts Work
        ↓
Status: IN_PROGRESS
        ↓
Agent Resolves Problem
        ↓
Status: RESOLVED
```

Agents should only manage complaints assigned to them.

---

# 6. Admin

An Admin manages the overall Complaint-Hub system.

## Responsibilities

An admin can:

- Log in
- View all complaints
- View complaint details
- View all users
- Manage agents
- Manage complaint categories
- Assign complaints to agents
- Monitor complaint progress
- View complaint statistics

The Admin is responsible for coordinating the complaint resolution process.

---

# 7. User Role Model

All system users will initially be represented using a common User model.

Each user will have a role.

The initial roles are:

```text
USER
AGENT
ADMIN
```

Conceptually:

```text
User
│
├── id
├── name
├── email
├── password
└── role
```

The role determines what operations the user is allowed to perform.

Later, the backend will implement authorization rules based on these roles.

---

# 8. Core Domain Objects

The initial domain model contains the following primary objects:

```text
Complaint-Hub
│
├── User
├── Complaint
├── Category
└── Complaint Update
```

These objects represent the core business concepts of the system.

---

# 9. User Domain Object

The User represents every person using the system.

Conceptually:

```text
User
├── id
├── name
├── email
├── password
├── role
├── createdAt
└── updatedAt
```

The User can represent:

```text
USER
AGENT
ADMIN
```

The exact database structure will be designed later.

---

# 10. Complaint Domain Object

The Complaint is the central domain object of Complaint-Hub.

A complaint is created by a user and progresses through different stages until it is resolved.

Conceptually:

```text
Complaint
├── id
├── title
├── description
├── priority
├── status
├── createdAt
├── updatedAt
├── createdBy
├── assignedAgent
└── category
```

## Relationships

A Complaint:

- Is created by one User
- Can be assigned to one Agent
- Belongs to one Category
- Can contain multiple updates

Conceptually:

```text
User
  │
  │ creates
  ▼
Complaint
  │
  ├── belongs to → Category
  │
  ├── assigned to → Agent
  │
  └── has many → Complaint Updates
```

---

# 11. Complaint Category

A Category is used to classify complaints.

Examples may include:

```text
Technical
Billing
Service
Product
Other
```

Categories help organize complaints and may later be used for:

- Filtering
- Reporting
- Assignment decisions
- Analytics

Initially, categories may be managed by an administrator.

Conceptually:

```text
Category
├── id
├── name
├── description
└── active
```

A category can contain multiple complaints.

```text
Category
    │
    └────── has many ────── Complaint
```

---

# 12. Complaint Priority

Priority represents the importance or urgency of a complaint.

The initial priority levels are:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Conceptually:

```text
LOW
  ↓
MEDIUM
  ↓
HIGH
  ↓
CRITICAL
```

Priority may later influence:

- Complaint ordering
- Agent response expectations
- Reporting
- Escalation rules

Priority is not currently considered a separate domain entity.

It will likely be represented using an enumeration.

Example:

```text
Priority
├── LOW
├── MEDIUM
├── HIGH
└── CRITICAL
```

---

# 13. Complaint Status

A complaint progresses through different stages during its lifecycle.

The initial statuses are:

```text
OPEN
ASSIGNED
IN_PROGRESS
RESOLVED
CLOSED
REJECTED
```

Each status represents the current state of a complaint.

---

# 14. Complaint Lifecycle

The primary complaint lifecycle is:

```text
OPEN
  │
  ▼
ASSIGNED
  │
  ▼
IN_PROGRESS
  │
  ▼
RESOLVED
  │
  ▼
CLOSED
```

An alternative path may be:

```text
OPEN
  │
  ▼
REJECTED
```

The complete conceptual lifecycle is:

```text
                    ┌──────────┐
                    │   OPEN   │
                    └────┬─────┘
                         │
                         ▼
                   ┌──────────┐
                   │ ASSIGNED │
                   └────┬─────┘
                         │
                         ▼
                 ┌─────────────┐
                 │ IN_PROGRESS │
                 └──────┬──────┘
                        │
                        ▼
                   ┌──────────┐
                   │ RESOLVED │
                   └────┬─────┘
                        │
                        ▼
                    ┌────────┐
                    │ CLOSED │
                    └────────┘
```

Alternative path:

```text
OPEN → REJECTED
```

The exact allowed transitions will be formally defined later as business rules.

---

# 15. Complaint Update

A Complaint Update represents an important update or activity related to a complaint.

Examples include:

- Agent added a progress update
- Agent added a resolution comment
- Complaint status changed
- Admin added an administrative update

Example:

```text
Complaint #101

Update 1:
Complaint created.

Update 2:
Complaint assigned to Agent A.

Update 3:
Agent started working on the issue.

Update 4:
Network connection restored.

Update 5:
Complaint resolved.
```

Conceptually:

```text
ComplaintUpdate
├── id
├── message
├── createdAt
├── createdBy
└── complaint
```

A complaint can have multiple updates.

```text
Complaint
    │
    └──── has many ──── ComplaintUpdate
```

This creates a history of important events and progress.

---

# 16. Complaint Assignment

For the initial version of the system, a Complaint can be assigned directly to an Agent.

Conceptually:

```text
Complaint
    │
    └── assignedAgent
```

Example:

```text
Complaint #101
        │
        ▼
Agent A
```

Assignment history is not included as a separate domain object in the initial version.

This decision keeps the first version simpler.

A separate Assignment entity may be introduced later if the application requires:

- Agent reassignment history
- Assignment timestamps
- Assignment reasons
- Multiple assignment records

---

# 17. Initial Domain Relationships

The current domain model can be represented as:

```text
                     USER
                      │
              creates │
                      ▼
                 COMPLAINT
                 /    │    \
                /     │     \
               ▼      ▼      ▼
        CATEGORY   AGENT   COMPLAINT UPDATE
                  assigned      │
                                │
                           created by
                                │
                                ▼
                               USER
```

Another simplified representation is:

```text
User
 │
 ├── creates ─────── Complaint
 │                       │
 │                       ├── belongs to ─── Category
 │                       │
 │                       ├── assigned to ─── Agent
 │                       │
 │                       └── contains ───── Complaint Updates
 │
 └── can create ───── Complaint Updates
```

---

# 18. Initial User Flow

The initial User flow is:

```text
Register / Login
        ↓
Create Complaint
        ↓
Complaint Status: OPEN
        ↓
View Complaint
        ↓
Track Progress
        ↓
View Updates
        ↓
Complaint Resolved
```

---

# 19. Initial Admin Flow

The initial Admin flow is:

```text
Login
   ↓
View Complaints
   ↓
Review Complaint
   ↓
Assign Complaint to Agent
   ↓
Monitor Progress
   ↓
Manage Complaint Resolution
```

The Admin may also manage:

```text
Users
Agents
Categories
```

---

# 20. Initial Agent Flow

The initial Agent workflow is:

```text
Login
   ↓
View Assigned Complaints
   ↓
Review Complaint Details
   ↓
Start Working
   ↓
Update Status
   ↓
Add Progress Updates
   ↓
Resolve Complaint
```

---

# 21. Initial Business Rules

The following business rules are proposed for the first version.

## Complaint Creation

- Only authenticated users can create complaints.
- Every complaint must contain a title.
- Every complaint must contain a description.
- Every complaint must belong to a category.
- Every new complaint starts with the status `OPEN`.
- Every complaint must have a priority.

---

## Complaint Assignment

- Only an Admin can assign a complaint to an Agent.
- A complaint can only have one currently assigned Agent.
- A complaint can be reassigned by an Admin.
- Assignment changes the complaint status from `OPEN` to `ASSIGNED`.

---

## Complaint Progress

- An Agent can only work on complaints assigned to them.
- An Agent can change an assigned complaint to `IN_PROGRESS`.
- An Agent can add updates to assigned complaints.
- Important progress should be recorded as a Complaint Update.

---

## Complaint Resolution

- An Agent can mark an assigned complaint as `RESOLVED`.
- A resolved complaint should contain resolution information.
- A resolved complaint may later be closed.

---

## Complaint Closure

- A complaint can move from `RESOLVED` to `CLOSED`.
- Closed complaints should not normally be modified.

The exact actor responsible for closing a complaint will be finalized during API and business rule design.

---

# 22. Domain Decisions

## Decision 1: Common User Model

All actors will initially use a common User model.

```text
User
│
└── Role
     ├── USER
     ├── AGENT
     └── ADMIN
```

Reason:

This avoids unnecessary duplication and simplifies authentication and authorization.

---

## Decision 2: Complaint Is the Central Domain Object

The Complaint represents the primary business object.

Most backend functionality will revolve around:

- Creating complaints
- Viewing complaints
- Assigning complaints
- Updating complaints
- Resolving complaints
- Tracking complaint history

---

## Decision 3: Priority Will Be an Enumeration

Priority is represented using predefined values:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Priority does not currently require a separate database entity.

---

## Decision 4: Status Will Be an Enumeration

Complaint status will initially use predefined values.

```text
OPEN
ASSIGNED
IN_PROGRESS
RESOLVED
CLOSED
REJECTED
```

The allowed transitions between statuses will later be enforced using business rules.

---

## Decision 5: Complaint Assignment Is Initially Simple

The initial version will store the currently assigned Agent directly on the Complaint.

Assignment history is outside the first version scope.

This keeps the initial design simpler.

---

# 23. Initial Scope

The first version of Complaint-Hub will focus on the following core functionality.

## User

- Authentication
- Create complaint
- View own complaints
- View complaint details
- Track complaint status

## Agent

- View assigned complaints
- Update complaint status
- Add complaint updates
- Resolve complaints

## Admin

- View all complaints
- Assign complaints to agents
- Manage categories
- Monitor complaint progress

---

# 24. Out of Scope for Initial Version

The following features are not part of the initial backend scope:

- Email notifications
- SMS notifications
- File attachments
- Advanced analytics
- Automatic complaint assignment
- AI-based complaint classification
- Escalation automation
- Assignment history
- Complex reporting
- Real-time notifications

These features may be added later after the core complaint management workflow is completed.

---

# 25. Current Domain Model Summary

The initial Complaint-Hub domain consists of:

```text
USER
AGENT
ADMIN
   │
   │ represented using
   ▼
USER
   │
   ├── creates
   ▼
COMPLAINT
   │
   ├── belongs to → CATEGORY
   │
   ├── has → PRIORITY
   │
   ├── has → STATUS
   │
   ├── assigned to → AGENT
   │
   └── contains → COMPLAINT UPDATES
```

---

# 26. Next Steps

The domain design phase establishes the business concepts of the application.

The next steps are:

```text
Domain Design
        ↓
Finalize Business Rules
        ↓
Define Entity Relationships
        ↓
Create Database Design
        ↓
Design Oracle Tables
        ↓
Configure Hibernate
        ↓
Create Java Entities
        ↓
Implement Persistence Layer
        ↓
Create Service Layer
        ↓
Create API Endpoints
```

The next development phase will focus on refining the domain model and converting the domain design into a database relationship design.