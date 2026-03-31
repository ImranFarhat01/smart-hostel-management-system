package com.hostel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity representing a complaint raised by a student.
 * Tracks complaint lifecycle: OPEN → IN_PROGRESS → RESOLVED / REJECTED.
 */
@Entity
@Table(name = "complaints", indexes = {
    @Index(name = "idx_complaint_student", columnList = "student_id"),
    @Index(name = "idx_complaint_status", columnList = "status"),
    @Index(name = "idx_complaint_category", columnList = "category"),
    @Index(name = "idx_complaint_priority", columnList = "priority")
})
@EntityListeners(AuditingEntityListener.class)
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotBlank(message = "Complaint title is required")
    @Size(min = 5, max = 200, message = "Title must be between 5 and 200 characters")
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 2000, message = "Description must be between 10 and 2000 characters")
    @Column(nullable = false, length = 2000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintCategory category = ComplaintCategory.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintPriority priority = ComplaintPriority.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ComplaintStatus status = ComplaintStatus.OPEN;

    @Column(length = 2000)
    private String resolution;

    @Column(length = 100)
    private String assignedTo;

    @Column
    private LocalDateTime resolvedAt;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── Enums ─────────────────────────────────────────────
    public enum ComplaintCategory {
        PLUMBING, ELECTRICAL, FURNITURE, CLEANLINESS, FOOD, NOISE,
        SECURITY, INTERNET, ROOMMATE, MAINTENANCE, GENERAL
    }

    public enum ComplaintPriority {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum ComplaintStatus {
        OPEN, IN_PROGRESS, RESOLVED, REJECTED, ESCALATED
    }

    // ── Business Logic ───────────────────────────────────
    public void markInProgress(String assignee) {
        if (this.status != ComplaintStatus.OPEN) {
            throw new IllegalStateException("Can only move OPEN complaints to IN_PROGRESS");
        }
        this.status = ComplaintStatus.IN_PROGRESS;
        this.assignedTo = assignee;
    }

    public void resolve(String resolutionNote) {
        if (this.status == ComplaintStatus.RESOLVED || this.status == ComplaintStatus.REJECTED) {
            throw new IllegalStateException("Complaint is already closed");
        }
        this.status = ComplaintStatus.RESOLVED;
        this.resolution = resolutionNote;
        this.resolvedAt = LocalDateTime.now();
    }

    public void reject(String reason) {
        if (this.status == ComplaintStatus.RESOLVED || this.status == ComplaintStatus.REJECTED) {
            throw new IllegalStateException("Complaint is already closed");
        }
        this.status = ComplaintStatus.REJECTED;
        this.resolution = "REJECTED: " + reason;
        this.resolvedAt = LocalDateTime.now();
    }

    public void escalate() {
        if (this.status == ComplaintStatus.RESOLVED || this.status == ComplaintStatus.REJECTED) {
            throw new IllegalStateException("Cannot escalate a closed complaint");
        }
        this.status = ComplaintStatus.ESCALATED;
        this.priority = ComplaintPriority.CRITICAL;
    }

    // ── Constructors ─────────────────────────────────────
    public Complaint() {}

    public Complaint(Student student, String title, String description,
                     ComplaintCategory category, ComplaintPriority priority) {
        this.student = student;
        this.title = title;
        this.description = description;
        this.category = category;
        this.priority = priority;
    }

    // ── Getters and Setters ──────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ComplaintCategory getCategory() { return category; }
    public void setCategory(ComplaintCategory category) { this.category = category; }

    public ComplaintPriority getPriority() { return priority; }
    public void setPriority(ComplaintPriority priority) { this.priority = priority; }

    public ComplaintStatus getStatus() { return status; }
    public void setStatus(ComplaintStatus status) { this.status = status; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Complaint{id=" + id + ", title='" + title + "', status=" + status +
               ", priority=" + priority + "}";
    }
}
