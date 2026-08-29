# 22. Finalized Business Rules

The following business rules were reviewed and finalized after the initial domain design.

---

## 22.1 Complaint Closure

An Agent is responsible for resolving a complaint.

However, an Agent cannot directly close a complaint.

The complaint lifecycle is:

```text
Agent resolves complaint
        ↓
Status: RESOLVED
        ↓
User reviews resolution
        ↓
User confirms solution
        ↓
Status: CLOSED
```

This allows the user who created the complaint to confirm that the problem has actually been resolved.

---

## 22.2 Complaint Resolution and Closure

An Agent is responsible for resolving a complaint.

The complaint lifecycle is:

```text
Agent resolves complaint
        ↓
Status: RESOLVED
        ↓
User reviews resolution
        ↓
User confirms solution
        ↓
Status: CLOSED
```

Once a complaint is resolved, it cannot be reopened.

If the user experiences the same or a related problem again, the user must create a new complaint.

This ensures that each complaint represents a separate complaint lifecycle and preserves the historical record of previously resolved issues.

---

## 22.3 Complaint Rejection

The `REJECTED` status will remain part of the complaint lifecycle.

A complaint may be rejected for reasons such as:

- Duplicate complaint
- Invalid complaint
- Insufficient information
- Complaint outside the supported scope

A rejection should include an explanation.

Example:

```text
Status: REJECTED

Reason:
This complaint is a duplicate of Complaint #102.
```

The rejection reason should be recorded as part of the complaint history.

---

## 22.4 Priority Management

A complaint has one of the following priority levels:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

A User selects the initial priority when creating a complaint.

An Admin can later modify the priority if required.

An Agent cannot change the priority in the initial version of the system.

The responsibility model is:

```text
USER
│
└── Sets initial priority

ADMIN
│
└── Can modify priority

AGENT
│
└── Cannot modify priority
```

---

## 22.5 Complaint Editing

After a complaint is submitted, the User cannot modify the original complaint details.

The original complaint should remain unchanged to preserve the original complaint record.

If the User wants to provide additional information, the User should add a Complaint Update.

Example:

```text
Original Complaint

Internet connection unavailable.

        ↓

User receives additional information

        ↓

Complaint Update

The connection started failing after a router restart.
```

This preserves the history of the complaint while allowing additional information to be provided.

---

# 23. Final Complaint Lifecycle

The finalized complaint lifecycle for the initial version is:

```text
                         ┌──────────┐
                         │   OPEN   │
                         └────┬─────┘
                              │
                    Admin assigns Agent
                              │
                              ▼
                        ┌──────────┐
                        │ ASSIGNED │
                        └────┬─────┘
                             │
                      Agent starts work
                             │
                             ▼
                      ┌─────────────┐
                      │ IN_PROGRESS │
                      └──────┬──────┘
                             │
                      Agent resolves issue
                             │
                             ▼
                        ┌──────────┐
                        │ RESOLVED │
                        └────┬─────┘
                             │
                    User confirms solution
                             │
                             ▼
                         ┌────────┐
                         │ CLOSED │
                         └────────┘
```

A complaint may also be rejected during the initial review stage.

```text
OPEN
 │
 ├── Assigned to Agent
 │
 └── Rejected by Admin
          │
          ▼
      REJECTED
```

Once a complaint reaches `RESOLVED`, it cannot return to a previous status.

If a similar or new issue occurs, the User must create a new Complaint.

---

# 24. Status Transition Rules

The allowed complaint status transitions are:

| Current Status | Allowed Next Status | Responsible Role |
|---|---|---|
| OPEN | ASSIGNED | ADMIN |
| OPEN | REJECTED | ADMIN |
| ASSIGNED | IN_PROGRESS | ASSIGNED AGENT |
| IN_PROGRESS | RESOLVED | ASSIGNED AGENT |
| RESOLVED | CLOSED | COMPLAINT OWNER |

The initial version does not allow arbitrary status changes.

For example:

```text
OPEN → RESOLVED
```

---

# 25. Permission Summary

The initial permission model is:

| Action | USER | AGENT | ADMIN |
|---|---|---|---|
| Create complaint | Yes | No | No |
| View own complaint | Yes | Assigned only | Yes |
| View all complaints | No | No | Yes |
| Assign complaint | No | No | Yes |
| Start working | No | Assigned only | No |
| Add complaint update | Own complaint | Assigned only | Yes |
| Resolve complaint | No | Assigned only | No |
| Close complaint | Own complaint | No | No |
| Reopen complaint | Own complaint | No | No |
| Reject complaint | No | No | Yes |
| Change priority | Initial value only | No | Yes |
| Edit original complaint | No | No | No |


# Next Steps

The domain design and core business rules have now been finalized.

The next phase is to convert the domain model into entity relationships.

The next development workflow will be:

```text
Finalized Domain Rules
        ↓
Entity Relationship Design
        ↓
Database Schema Design
        ↓
Oracle Database Setup
        ↓
Hibernate Configuration
        ↓
Java Entity Implementation


---

# Step 3: Read the document once like a reviewer

Before committing, don't immediately run Git commands.

Read the document and verify these questions:

### Complaint lifecycle

- Does every complaint start as `OPEN`?
- Can only Admin assign an agent?
- Can only the assigned Agent start work?
- Can only the assigned Agent resolve?
- Can only the complaint owner close?
- Can the owner reopen after resolution?

### Rejected complaint

- Who can reject it?
- Is a rejection reason required?

### Priority

- Who sets the initial priority?
- Who can change it later?

### Complaint updates

- Can the original complaint be edited?
- How does a user provide additional information?

This is actually an important industry habit: **review requirements before implementing them**.

---

# Step 4: Check Git changes

Run:

```powershell
git status