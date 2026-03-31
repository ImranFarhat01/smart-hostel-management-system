package com.hostel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entity representing a fee record for a student.
 * Tracks total amount, paid amount, payment status, and due dates.
 */
@Entity
@Table(name = "fees", indexes = {
    @Index(name = "idx_fee_student", columnList = "student_id"),
    @Index(name = "idx_fee_status", columnList = "paymentStatus"),
    @Index(name = "idx_fee_due_date", columnList = "dueDate")
})
@EntityListeners(AuditingEntityListener.class)
public class Fee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.0", message = "Total amount must be non-negative")
    @Column(nullable = false)
    private Double totalAmount;

    @Column(nullable = false)
    private Double paidAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FeeType feeType = FeeType.HOSTEL_RENT;

    @NotBlank(message = "Semester/period is required")
    @Column(nullable = false, length = 30)
    private String semester;

    @NotNull(message = "Due date is required")
    @Column(nullable = false)
    private LocalDate dueDate;

    @Column
    private LocalDate paymentDate;

    @Column(length = 50)
    private String transactionId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod paymentMethod;

    @Column(length = 500)
    private String remarks;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── Enums ─────────────────────────────────────────────
    public enum PaymentStatus {
        PENDING, PARTIAL, PAID, OVERDUE, WAIVED
    }

    public enum FeeType {
        HOSTEL_RENT, MESS_FEE, SECURITY_DEPOSIT, MAINTENANCE, ELECTRICITY, LAUNDRY, OTHER
    }

    public enum PaymentMethod {
        CASH, UPI, BANK_TRANSFER, CHEQUE, ONLINE, CARD
    }

    // ── Business Logic ───────────────────────────────────
    public Double getBalanceAmount() {
        return this.totalAmount - this.paidAmount;
    }

    public void makePayment(Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        if (amount > getBalanceAmount()) {
            throw new IllegalArgumentException("Payment amount ₹" + amount +
                " exceeds balance ₹" + getBalanceAmount());
        }
        this.paidAmount += amount;
        recalculateStatus();
    }

    public void recalculateStatus() {
        if (this.paidAmount >= this.totalAmount) {
            this.paymentStatus = PaymentStatus.PAID;
            this.paymentDate = LocalDate.now();
        } else if (this.paidAmount > 0) {
            this.paymentStatus = PaymentStatus.PARTIAL;
        } else if (LocalDate.now().isAfter(this.dueDate)) {
            this.paymentStatus = PaymentStatus.OVERDUE;
        } else {
            this.paymentStatus = PaymentStatus.PENDING;
        }
    }

    public boolean isOverdue() {
        return paymentStatus != PaymentStatus.PAID &&
               paymentStatus != PaymentStatus.WAIVED &&
               LocalDate.now().isAfter(dueDate);
    }

    // ── Constructors ─────────────────────────────────────
    public Fee() {}

    public Fee(Student student, Double totalAmount, FeeType feeType,
               String semester, LocalDate dueDate) {
        this.student = student;
        this.totalAmount = totalAmount;
        this.feeType = feeType;
        this.semester = semester;
        this.dueDate = dueDate;
    }

    // ── Getters and Setters ──────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public Double getPaidAmount() { return paidAmount; }
    public void setPaidAmount(Double paidAmount) { this.paidAmount = paidAmount; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public FeeType getFeeType() { return feeType; }
    public void setFeeType(FeeType feeType) { this.feeType = feeType; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Fee{id=" + id + ", student=" + (student != null ? student.getName() : "N/A") +
               ", total=" + totalAmount + ", paid=" + paidAmount + ", status=" + paymentStatus + "}";
    }
}
