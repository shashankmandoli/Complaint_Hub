# Complaint-Hub Entity Relationship Design

This document defines the relationships between the core entities of the Complaint-Hub application.

The purpose of this document is to convert the business domain defined in `domain-design.md` into a conceptual entity relationship model.

This design will later be used to create:

- Oracle database tables
- Primary keys
- Foreign keys
- Hibernate entity relationships
- Java domain models

This document focuses on entity relationships and cardinality rather than database-specific implementation details.

---

# 1. Design Workflow

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

The system also contains several enumerated values:

```text
ROLE
PRIORITY
COMPLAINT_STATUS
```

These enumerations are currently considered value types rather than independent entities.

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

The User entity represents all three system actors.

```text
                    USER
                      │
          ┌───────────┼───────────┐
          │           │           │
          ▼           ▼           ▼
        USER        AGENT       ADMIN
```

The role determines what operations the User is allowed to perform.

---

# 4. Category Entity

The Category entity represents a classification for complaints.

Examples:

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

---

# 5. Complaint Entity

The Complaint is the central entity of the Complaint-Hub system.

A Complaint is created by a User and progresses through the complaint lifecycle until it is resolved and closed.

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

---

# 6. ComplaintUpdate Entity

The ComplaintUpdate entity represents an update or additional information related to a Complaint.

Updates can be created by different users depending on their role.

Examples include:

- User provides additional information
- Agent provides a progress update
- Agent provides resolution details
- Admin provides an administrative update
- User requests reopening of a complaint

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

Each Complaint Update belongs to one Complaint.

---

# 7. Enumerated Value Types

The following values are not separate entities in the initial design.

---

## 7.1 Role

```text
USER
AGENT
ADMIN
```

Role determines the permissions of a User.

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

Complaint Status represents the current state of a Complaint.

The allowed transitions are controlled by business rules.

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

This is a:

```text
One-to-Many relationship
```

The Complaint stores a reference to the User who created it.

Conceptually:

```text
Complaint
│
└── createdBy → User
```

---

# 10. User to Complaint Assigned Agent Relationship

An Agent is represented using the User entity with:

```text
role = AGENT
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

A Complaint may initially have no assigned Agent while its status is:

```text
OPEN
```

Therefore, the assigned Agent relationship is optional during complaint creation.

---

# 11. Important User and Complaint Relationship Distinction

The Complaint entity has two different relationships with the User entity.

```text
Complaint
├── createdBy → User
└── assignedAgent → User
```

Although both relationships point to the same User entity, they represent different business meanings.

## createdBy

Represents the User who originally created the Complaint.

Example:

```text
Complaint #101
Created By: John
```

## assignedAgent

Represents the Agent currently responsible for resolving the Complaint.

Example:

```text
Complaint #101
Assigned Agent: Alice
```

These relationships must be treated separately in both the database design and Hibernate entity mapping.

---

# 12. Category to Complaint Relationship

A Category can contain multiple Complaints.

Each Complaint belongs to one Category.

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

This is a One-to-Many relationship.

Conceptually:

```text
Complaint
│
└── category → Category
```

---

# 13. Complaint to ComplaintUpdate Relationship

A Complaint can contain multiple Complaint Updates.

Each Complaint Update belongs to exactly one Complaint.

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

This relationship allows the application to maintain the history and progress of a Complaint.

---

# 14. User to ComplaintUpdate Relationship

A User can create multiple Complaint Updates.

Each Complaint Update is created by exactly one User.

The User may have different roles.

Examples:

```text
USER
│
└── Provides additional information


AGENT
│
└── Provides progress or resolution updates


ADMIN
│
└── Provides administrative updates
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

---

# 15. Relationship Cardinality Summary

The following table summarizes the initial relationships.

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

# 16. Optional and Required Relationships

Not every relationship has the same requirement.

---

## Complaint Creator

A Complaint must always have a creator.

```text
Complaint → createdBy → Required
```

A Complaint cannot exist without knowing which User created it.

---

## Complaint Category

A Complaint must always belong to a Category.

```text
Complaint → category → Required
```

---

## Assigned Agent

A Complaint does not require an assigned Agent when first created.

```text
OPEN Complaint
      │
      └── assignedAgent = null
```

After an Admin assigns the Complaint:

```text
Complaint
│
└── assignedAgent → Agent
```

Therefore:

```text
Complaint → assignedAgent → Optional
```

---

## Complaint Updates

A Complaint may initially have no additional updates.

```text
Complaint
│
└── updates → 0..*
```

However, every ComplaintUpdate must belong to exactly one Complaint.

```text
ComplaintUpdate
│
└── complaint → Required
```

---

## ComplaintUpdate Creator

Every ComplaintUpdate must have a creator.

```text
ComplaintUpdate
│
└── createdBy → Required
```

---

# 17. Conceptual Entity Relationship Diagram

The complete initial conceptual relationship model is:

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
              ┌───────────────────────┼────────────────────────┐
              │                       │                        │
              │ creates               │ assigned as            │ creates
              │                       │ Agent                  │
              ▼                       ▼                        ▼
       ┌───────────────┐        ┌───────────────┐      ┌───────────────────┐
       │   COMPLAINT   │◄───────│     USER      │─────►│ COMPLAINT_UPDATE  │
       ├───────────────┤        └───────────────┘      ├───────────────────┤
       │ id            │                               │ id                │
       │ title         │                               │ message           │
       │ description   │                               │ createdAt         │
       │ priority      │                               │ createdBy         │
       │ status        │                               │ complaint         │
       │ createdAt     │                               └─────────┬─────────┘
       │ updatedAt     │                                         │
       │ createdBy     │                                         │ belongs to
       │ assignedAgent │◄────────────────────────────────────────┘
       │ category      │
       └───────┬───────┘
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

The diagram is conceptual and will be refined when designing the physical database schema.

---

# 18. Relationship Rules

The entity relationships must follow the following rules.

## User and Complaint

- A User can create multiple Complaints.
- Every Complaint has exactly one creator.
- A Complaint creator cannot be null.

---

## Agent and Complaint

- An Agent can be assigned multiple Complaints.
- A Complaint can have only one currently assigned Agent.
- A Complaint may initially have no assigned Agent.
- The assigned User must have the `AGENT` role.
- Assignment history is not part of the initial version.

---

## Category and Complaint

- A Category can contain multiple Complaints.
- Every Complaint must belong to one Category.
- A Complaint cannot exist without a Category.

---

## Complaint and ComplaintUpdate

- A Complaint can have zero or more Complaint Updates.
- Every ComplaintUpdate belongs to one Complaint.
- A ComplaintUpdate cannot exist independently without a Complaint.

---

## User and ComplaintUpdate

- A User can create multiple Complaint Updates.
- Every ComplaintUpdate has exactly one creator.
- The creator may be a User, Agent, or Admin.

---

# 19. Design Decisions

## Decision 1: Agent Is Not a Separate Entity

Agents are represented using the User entity.

```text
User
│
└── role = AGENT
```

This avoids creating duplicate entities for Users, Agents, and Admins.

---

## Decision 2: Complaint Has Two User References

The Complaint entity contains two references to User.

```text
createdBy
assignedAgent
```

These references represent different relationships and business responsibilities.

---

## Decision 3: Assignment History Is Not Included Initially

The system stores only the currently assigned Agent.

```text
Complaint
│
└── assignedAgent
```

Historical assignments may be introduced later if required.

---

## Decision 4: ComplaintUpdate Preserves History

Complaint updates are stored separately instead of modifying the original Complaint.

This allows the system to preserve:

- Additional user information
- Agent progress updates
- Resolution information
- Administrative updates
- Reopening information

---

## Decision 5: Enumerations Are Not Separate Entities

The following concepts will initially be represented as enumerated values:

```text
Role
Priority
ComplaintStatus
```

Separate database tables are not required for these values in the initial version.

---

# 20. Initial Data Ownership Model

The ownership of important data is defined as follows:

```text
USER
│
├── owns
│     └── Created Complaints
│
├── may be assigned
│     └── Complaints as Agent
│
└── creates
      └── Complaint Updates
```

The Complaint acts as the central business object.

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

The current relationship model is intentionally kept simple.

Possible future additions include:

```text
Complaint
│
├── AssignmentHistory
│
├── Attachment
│
├── Notification
│
├── Escalation
│
└── Resolution
```

These are outside the initial scope.

The backend will first implement the core complaint management workflow.

---

# 22. Final Relationship Summary

The initial Complaint-Hub entity relationship model contains:

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

The Complaint entity acts as the central entity of the application.

The initial design contains four primary entities:

```text
USER
CATEGORY
COMPLAINT
COMPLAINT_UPDATE
```

---

# 23. Next Steps

The entity relationships are now defined conceptually.

The next phase is to convert this conceptual design into a physical database schema.

The next workflow will be:

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

The next development phase will focus on Oracle database schema design.