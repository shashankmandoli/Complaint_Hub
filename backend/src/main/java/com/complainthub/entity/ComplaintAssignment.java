package com.complainthub.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ComplaintAssignment {
    @Id
    @SequenceGenerator(
            name = "complaint_assignment_sequence",
            sequenceName = "COMPLAINT_ASSIGNMENT_SEQUENCE",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "complaint_assignment_sequence"
    )
    private long id;

    @ManyToOne
    @JoinColumn(
            name = "complaint_id",
            nullable = false
    )
    private Complaint complaint;

    @ManyToOne
    @JoinColumn(
            name = "agent_id",
            nullable = false
    )
    private User agent;

    @ManyToOne
    @JoinColumn(
            name = "assigned_by",
            nullable = false
    )
    private User assignedBy;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public ComplaintAssignment(){
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public void setComplaint(Complaint complaint) {
        this.complaint = complaint;
    }

    public User getAgent() {
        return agent;
    }

    public void setAgent(User agent) {
        this.agent = agent;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
    }

    public boolean isActive() { return active; }

    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
