package com.complainthub.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ComplaintUpdate {
    @Id
    @SequenceGenerator(
            name = "complaint_update_sequence",
            sequenceName = "COMPLAINT_UPDATE_SEQUENCE",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "complaint_update_sequence"
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
            name = "updated_by",
            nullable = false
    )
    private User updatedBy;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(nullable = false)
    private boolean visibleToUser;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ComplaintUpdate() {
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

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean getVisibleToUser() {
        return visibleToUser;
    }

    public void setVisibleToUser(boolean visibleToUser) {
        this.visibleToUser = visibleToUser;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
