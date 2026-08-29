# Complaint-Hub Entity Relationship Design

This document defines the relationships between the core entities of the Complaint-Hub application.

The purpose of this document is to convert the business domain defined in `domain-design.md` into a conceptual entity relationship model.

This design will later be used to create:

- Oracle database tables
- Primary keys
- Foreign keys
- Database constraints
- Hibernate entity relationships
- Java domain models

This document focuses on entity relationships and cardinality rather than database-specific implementation details.

---

# 1. Development Workflow

The Complaint-Hub backend development follows the workflow:

```text
Business Requirements
        ↓
Domain Design
        ↓
Entity Relationship Design
        ↓
Database Schema Design
        ↓
Oracle Database
        ↓
Hibernate Entity Mapping
        ↓
Backend Implementation
```

The current phase focuses on understanding how the core domain entities relate to each other.

---

# 2. Core Entities

The initial Complaint-Hub system contains four primary entities:

```text
USER
CATEGORY
COMPLAINT
COMPLAINT_UPDATE
```

The system also contains several enumerated value types:

```text
ROLE
PRIORITY
COMPLAINT_STATUS
```

These value types are not treated as independent entities in the initial design.

---

# 3. User Entity

The User entity represents every person who interacts with the Complaint-Hub system.

A User may have one of the following roles:

```text
USER
AGENT
ADMIN
```

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

The same User entity represents all system actors.

```text
                    USER
                      │
          ┌───────────┼───────────┐
          │           │           │
          ▼           ▼           ▼
        USER        AGENT       ADMIN
```

The role determines the operations and permissions available to the user.

---

# 4. Category Entity

The Category entity represents the classification of a Complaint.

Examples may include:

```text
Technical
Billing
Service
Product
Other
```

Conceptually:

```text
Category
├── id
├── name
├── description
└── active
```

A Category can contain multiple Complaints.

Each Complaint belongs to one Category.

---

# 5. Complaint Entity

The Complaint is the central entity of the Complaint-Hub application.

A Complaint is created by a User and progresses through the defined complaint lifecycle.

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

A Complaint has relationships with:

- User as the complaint creator
- User as the assigned Agent
- Category
- Complaint Updates

The Complaint lifecycle is:

```text
OPEN
  ↓
ASSIGNED
  ↓
IN_PROGRESS
  ↓
RESOLVED
  ↓
CLOSED
```

An alternative path is:

```text
OPEN
  ↓
REJECTED
```

A resolved or closed Complaint cannot be reopened.

If the User experiences the same or a similar problem again, a new Complaint must be created.

---

# 6. ComplaintUpdate Entity

The ComplaintUpdate entity represents an update or additional information related to a Complaint.

Updates allow the system to maintain a history of important events and communication.

Examples include:

- User provides additional information
- Agent provides a progress update
- Agent provides resolution details
- Admin provides an administrative update
- Admin provides a rejection reason

Conceptually:

```text
ComplaintUpdate
├── id
├── message
├── createdAt
├── createdBy
└── complaint
```

A Complaint can contain multiple Complaint Updates.

Each ComplaintUpdate belongs to exactly one Complaint.

---

# 7. Enumerated Value Types

The following concepts are represented as predefined values rather than separate entities.

---

## 7.1 Role

```text
USER
AGENT
ADMIN
```

Role determines the permissions available to a User.

---

## 7.2 Priority

```text
LOW
MEDIUM
HIGH
CRITICAL
```

Priority represents the urgency of a Complaint.

---

## 7.3 Complaint Status

```text
OPEN
ASSIGNED
IN_PROGRESS
RESOLVED
CLOSED
REJECTED
```

Complaint Status represents the current stage of a Complaint.

The allowed status transitions are controlled by business rules.

---

# 8. Entity Relationships Overview

The initial entity relationships are:

```text
USER
 │
 ├── creates ──────────────── COMPLAINT
 │
 ├── assigned to ──────────── COMPLAINT
 │
 └── creates ──────────────── COMPLAINT_UPDATE


CATEGORY
 │
 └── contains ─────────────── COMPLAINT


COMPLAINT
 │
 └── contains ─────────────── COMPLAINT_UPDATE
```

The relationships are explained in detail below.

---

# 9. User to Complaint Creator Relationship

A User can create multiple Complaints.

Each Complaint is created by exactly one User.

Example:

```text
User: John

├── Complaint #101
├── Complaint #102
└── Complaint #103
```

Relationship:

```text
USER 1 ─────────────── * COMPLAINT
```

From the Complaint perspective:

```text
COMPLAINT * ─────────── 1 USER
```

This is a One-to-Many relationship.

Conceptually:

```text
Complaint
│
└── createdBy → User
```

The Complaint creator is required.

A Complaint cannot exist without knowing which User created it.

---

# 10. User to Complaint Assigned Agent Relationship

An Agent is not represented as a separate entity.

An Agent is represented as:

```text
User
│
└── role = AGENT
```

One Agent can be assigned multiple Complaints.

However, a Complaint can have only one currently assigned Agent.

Example:

```text
Agent A

├── Complaint #101
├── Complaint #102
└── Complaint #103
```

Relationship:

```text
USER (AGENT) 1 ─────────────── * COMPLAINT
```

From the Complaint perspective:

```text
COMPLAINT * ─────────── 1 USER (AGENT)
```

Conceptually:

```text
Complaint
│
└── assignedAgent → User
```

The assigned Agent is optional when the Complaint is first created.

Example:

```text
New Complaint

Status: OPEN
Assigned Agent: None
```

After assignment:

```text
Complaint

Status: ASSIGNED
Assigned Agent: Agent A
```

---

# 11. Complaint User Relationship Distinction

The Complaint entity has two different relationships with the User entity.

```text
Complaint
├── createdBy → User
└── assignedAgent → User
```

Although both relationships point to the User entity, they represent different business responsibilities.

---

## createdBy

Represents the User who originally created the Complaint.

Example:

```text
Complaint #101

Created By:
John
```

---

## assignedAgent

Represents the Agent currently responsible for resolving the Complaint.

Example:

```text
Complaint #101

Assigned Agent:
Alice
```

These relationships must remain separate in the database design and Hibernate entity mapping.

---

# 12. Category to Complaint Relationship

A Category can contain multiple Complaints.

Each Complaint belongs to exactly one Category.

Example:

```text
Category: Technical

├── Complaint #101
├── Complaint #102
└── Complaint #103
```

Relationship:

```text
CATEGORY 1 ─────────────── * COMPLAINT
```

From the Complaint perspective:

```text
COMPLAINT * ─────────── 1 CATEGORY
```

Conceptually:

```text
Complaint
│
└── category → Category
```

The Category relationship is required.

Every Complaint must belong to a Category.

---

# 13. Complaint to ComplaintUpdate Relationship

A Complaint can contain multiple Complaint Updates.

Each ComplaintUpdate belongs to exactly one Complaint.

Example:

```text
Complaint #101

├── Update #1
├── Update #2
├── Update #3
└── Update #4
```

Relationship:

```text
COMPLAINT 1 ─────────────── * COMPLAINT_UPDATE
```

From the ComplaintUpdate perspective:

```text
COMPLAINT_UPDATE * ─────────── 1 COMPLAINT
```

Conceptually:

```text
Complaint
│
└── updates → List<ComplaintUpdate>
```

A Complaint may initially have no updates.

Therefore:

```text
Complaint → ComplaintUpdate

0..*
```

However, every ComplaintUpdate must belong to exactly one Complaint.

A ComplaintUpdate cannot exist independently.

---

# 14. User to ComplaintUpdate Relationship

A User can create multiple Complaint Updates.

Each ComplaintUpdate is created by exactly one User.

The creator can have different roles.

Examples:

```text
USER
│
└── Provides additional information


AGENT
│
└── Provides progress updates
    and resolution information


ADMIN
│
└── Provides administrative updates
    or rejection reasons
```

Relationship:

```text
USER 1 ─────────────── * COMPLAINT_UPDATE
```

From the ComplaintUpdate perspective:

```text
COMPLAINT_UPDATE * ─────────── 1 USER
```

Conceptually:

```text
ComplaintUpdate
│
└── createdBy → User
```

Every ComplaintUpdate must have a creator.

---

# 15. Relationship Cardinality Summary

The following table summarizes the initial entity relationships.

| Entity A | Relationship | Entity B | Cardinality |
|---|---|---|---|
| User | Creates | Complaint | One-to-Many |
| User (Agent) | Assigned to | Complaint | One-to-Many |
| Category | Contains | Complaint | One-to-Many |
| Complaint | Contains | ComplaintUpdate | One-to-Many |
| User | Creates | ComplaintUpdate | One-to-Many |

From the reverse perspective:

| Entity A | Relationship | Entity B | Cardinality |
|---|---|---|---|
| Complaint | Created by | User | Many-to-One |
| Complaint | Assigned Agent | User | Many-to-One |
| Complaint | Belongs to | Category | Many-to-One |
| ComplaintUpdate | Belongs to | Complaint | Many-to-One |
| ComplaintUpdate | Created by | User | Many-to-One |

---

# 16. Required and Optional Relationships

Not every relationship has the same requirement.

---

## 16.1 Complaint Creator

A Complaint must always have a creator.

```text
Complaint
│
└── createdBy → Required
```

---

## 16.2 Complaint Category

A Complaint must always belong to a Category.

```text
Complaint
│
└── category → Required
```

---

## 16.3 Assigned Agent

A Complaint does not require an assigned Agent when it is first created.

```text
OPEN Complaint

assignedAgent = null
```

Therefore:

```text
Complaint
│
└── assignedAgent → Optional
```

After assignment, the assigned Agent becomes responsible for handling the Complaint.

---

## 16.4 Complaint Updates

A Complaint may have zero or more Complaint Updates.

```text
Complaint
│
└── updates → 0..*
```

However:

```text
ComplaintUpdate
│
└── complaint → Required
```

---

## 16.5 ComplaintUpdate Creator

Every ComplaintUpdate must have a creator.

```text
ComplaintUpdate
│
└── createdBy → Required
```

---

# 17. Conceptual Entity Relationship Diagram

The complete conceptual entity relationship model is:

```text
                              ┌──────────────────┐
                              │       USER       │
                              ├──────────────────┤
                              │ id               │
                              │ name             │
                              │ email            │
                              │ password         │
                              │ role             │
                              │ createdAt        │
                              │ updatedAt        │
                              └────────┬─────────┘
                                       │
             ┌─────────────────────────┼─────────────────────────┐
             │                         │                         │
             │ creates                 │ assigned as Agent       │ creates
             │                         │                         │
             ▼                         ▼                         ▼

       ┌─────────────────────────────────────┐       ┌─────────────────────┐
       │              COMPLAINT              │       │  COMPLAINT_UPDATE   │
       ├─────────────────────────────────────┤       ├─────────────────────┤
       │ id                                  │◄──────│ complaint           │
       │ title                               │       │ id                  │
       │ description                         │       │ message             │
       │ priority                            │       │ createdAt           │
       │ status                              │       │ createdBy           │
       │ createdAt                           │       └─────────────────────┘
       │ updatedAt                           │
       │ createdBy → USER                    │
       │ assignedAgent → USER                │
       │ category → CATEGORY                 │
       └──────────────────┬──────────────────┘
                          │
                          │ belongs to
                          ▼
                   ┌───────────────┐
                   │   CATEGORY    │
                   ├───────────────┤
                   │ id            │
                   │ name          │
                   │ description   │
                   │ active        │
                   └───────────────┘
```

The diagram represents the conceptual entity relationships.

The physical database schema will later define the actual primary keys and foreign keys.

---

# 18. Relationship Rules

The entity relationships must follow the following rules.

---

## User and Complaint

- A User can create multiple Complaints.
- Every Complaint has exactly one creator.
- The Complaint creator cannot be null.

---

## Agent and Complaint

- An Agent can be assigned multiple Complaints.
- A Complaint can have only one currently assigned Agent.
- A Complaint may initially have no assigned Agent.
- The assigned User must have the `AGENT` role.
- Assignment history is not included in the initial version.

---

## Category and Complaint

- A Category can contain multiple Complaints.
- Every Complaint must belong to one Category.
- A Complaint cannot exist without a Category.

---

## Complaint and ComplaintUpdate

- A Complaint can have zero or more Complaint Updates.
- Every ComplaintUpdate belongs to one Complaint.
- A ComplaintUpdate cannot exist independently.

---

## User and ComplaintUpdate

- A User can create multiple Complaint Updates.
- Every ComplaintUpdate has exactly one creator.
- The creator may have the role `USER`, `AGENT`, or `ADMIN`.

---

# 19. Design Decisions

## Decision 1: Common User Entity

Users, Agents, and Admins are represented using a common User entity.

```text
User
│
└── role
     ├── USER
     ├── AGENT
     └── ADMIN
```

This avoids duplicate entities and simplifies authentication and authorization.

---

## Decision 2: Complaint Has Two User Relationships

The Complaint entity contains two separate references to User.

```text
createdBy
assignedAgent
```

These relationships represent different responsibilities.

```text
createdBy
    ↓
Who created the Complaint

assignedAgent
    ↓
Who is responsible for resolving the Complaint
```

---

## Decision 3: Assignment History Is Not Included

The initial version stores only the currently assigned Agent.

```text
Complaint
│
└── assignedAgent
```

Historical assignments are outside the initial project scope.

---

## Decision 4: ComplaintUpdate Preserves History

Complaint updates are stored separately from the original Complaint.

This preserves:

- Additional information from Users
- Agent progress updates
- Resolution information
- Administrative updates
- Rejection reasons

The original Complaint details remain unchanged after creation.

---

## Decision 5: Enumerations Are Not Separate Entities

The following concepts are represented using predefined values:

```text
Role
Priority
ComplaintStatus
```

Separate database entities are not required for these concepts in the initial version.

---

## Decision 6: Complaints Cannot Be Reopened

Each Complaint represents a single complaint lifecycle.

The lifecycle is:

```text
OPEN
  ↓
ASSIGNED
  ↓
IN_PROGRESS
  ↓
RESOLVED
  ↓
CLOSED
```

A Complaint may alternatively be rejected while in the `OPEN` state.

```text
OPEN
  ↓
REJECTED
```

Once a Complaint reaches `RESOLVED` or `CLOSED`, it cannot return to a previous state.

If a User experiences the same or a similar problem again, the User must create a new Complaint.

This preserves the history of completed complaint lifecycles and keeps the initial workflow simple.

---

# 20. Initial Data Ownership Model

The ownership of important data is conceptually represented as:

```text
USER
│
├── creates
│     │
│     └── COMPLAINT
│
├── may be assigned
│     │
│     └── COMPLAINT
│
└── creates
      │
      └── COMPLAINT_UPDATE
```

The Complaint acts as the central business entity.

```text
                    USER
                     │
                     │ creates
                     ▼
                COMPLAINT
                /    |    \
               /     |     \
              ▼      ▼      ▼
        CATEGORY   AGENT   UPDATES
```

---

# 21. Future Relationship Extensions

The current relationship model is intentionally simple.

Possible future entities may include:

```text
Complaint
│
├── AssignmentHistory
├── Attachment
├── Notification
├── Escalation
└── Resolution
```

These entities are outside the scope of the initial version.

The first version will focus on the core complaint management workflow.

---

# 22. Final Relationship Summary

The Complaint-Hub entity relationship model contains the following core relationships:

```text
USER
 │
 ├── creates ──────────────── COMPLAINT
 │
 ├── assigned to ──────────── COMPLAINT
 │
 └── creates ──────────────── COMPLAINT_UPDATE


CATEGORY
 │
 └── contains ─────────────── COMPLAINT


COMPLAINT
 │
 └── contains ─────────────── COMPLAINT_UPDATE
```

The Complaint is the central entity of the system.

The initial design contains four primary entities:

```text
USER
CATEGORY
COMPLAINT
COMPLAINT_UPDATE
```

---

# 23. Next Steps

The conceptual entity relationships are now defined.

The next phase is to convert this design into a physical database schema.

The next development workflow will be:

```text
Entity Relationship Design
        ↓
Database Schema Design
        ↓
Define Tables
        ↓
Define Columns
        ↓
Define Primary Keys
        ↓
Define Foreign Keys
        ↓
Define Constraints
        ↓
Create Oracle Database Schema
        ↓
Configure Hibernate
```

The next development phase will focus on designing the Oracle database schema for Complaint-Hub.