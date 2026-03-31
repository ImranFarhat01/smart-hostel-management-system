package com.hostel.service;

import com.hostel.model.Fee;
import com.hostel.model.Fee.FeeType;
import com.hostel.model.Fee.PaymentMethod;
import com.hostel.model.Fee.PaymentStatus;
import com.hostel.model.Student;
import com.hostel.repository.FeeRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

/**
 * Business logic for fee management.
 * Handles fee generation, payment processing, and financial reporting.
 */
@Service
@Transactional
public class FeeService {

    private static final Logger logger = Logger.getLogger(FeeService.class.getName());

    private final FeeRepository feeRepository;
    private final StudentRepository studentRepository;

    public FeeService(FeeRepository feeRepository, StudentRepository studentRepository) {
        this.feeRepository = feeRepository;
        this.studentRepository = studentRepository;
    }

    // ── Fee Creation ─────────────────────────────────────

    public Fee createFee(Long studentId, Double totalAmount, FeeType feeType,
                         String semester, LocalDate dueDate) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        Fee fee = new Fee(student, totalAmount, feeType, semester, dueDate);
        Fee saved = feeRepository.save(fee);

        logger.info("💰 Fee created: ₹" + totalAmount + " (" + feeType + ") for " + student.getName());
        return saved;
    }

    /**
     * Generates bulk fees for all active students for a given semester.
     */
    public List<Fee> generateBulkFees(Double amount, FeeType feeType,
                                       String semester, LocalDate dueDate) {
        List<Student> activeStudents = studentRepository.findByStatus(Student.StudentStatus.ACTIVE);
        List<Fee> generatedFees = new ArrayList<>();

        for (Student student : activeStudents) {
            Fee fee = new Fee(student, amount, feeType, semester, dueDate);
            generatedFees.add(feeRepository.save(fee));
        }

        logger.info("📋 Bulk fees generated: " + generatedFees.size() + " records for " + semester);
        return generatedFees;
    }

    // ── Payment Processing ───────────────────────────────

    /**
     * Process a payment against a fee record.
     * Updates fee status automatically based on paid vs total amount.
     */
    public Fee makePayment(Long feeId, Double amount, PaymentMethod method, String transactionId) {
        Fee fee = getFeeById(feeId);

        fee.makePayment(amount);
        fee.setPaymentMethod(method);
        fee.setTransactionId(transactionId);

        if (fee.getPaymentStatus() == PaymentStatus.PAID) {
            fee.setPaymentDate(LocalDate.now());
        }

        Fee updated = feeRepository.save(fee);
        logger.info("💳 Payment received: ₹" + amount + " for Fee #" + feeId +
            " | Status: " + updated.getPaymentStatus());
        return updated;
    }

    public Fee waiveFee(Long feeId, String reason) {
        Fee fee = getFeeById(feeId);
        fee.setPaymentStatus(PaymentStatus.WAIVED);
        fee.setRemarks("WAIVED: " + reason);
        fee.setPaymentDate(LocalDate.now());

        logger.info("🎫 Fee waived: #" + feeId + " | Reason: " + reason);
        return feeRepository.save(fee);
    }

    // ── Queries ──────────────────────────────────────────

    public Fee getFeeById(Long id) {
        return feeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee record not found: " + id));
    }

    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }

    public List<Fee> getFeesByStudent(Long studentId) {
        return feeRepository.findByStudentId(studentId);
    }

    public List<Fee> getPendingFees(Long studentId) {
        return feeRepository.findPendingFeesByStudent(studentId);
    }

    public List<Fee> getOverdueFees() {
        return feeRepository.findOverdueFees(LocalDate.now());
    }

    public List<Fee> getFeesByStatus(PaymentStatus status) {
        return feeRepository.findByPaymentStatus(status);
    }

    public List<Fee> getFeesBySemester(String semester) {
        return feeRepository.findBySemester(semester);
    }

    /**
     * Refreshes overdue status for all pending fees past due date.
     */
    public void refreshOverdueStatuses() {
        List<Fee> overdue = feeRepository.findOverdueFees(LocalDate.now());
        int count = 0;
        for (Fee fee : overdue) {
            if (fee.getPaymentStatus() != PaymentStatus.OVERDUE) {
                fee.setPaymentStatus(PaymentStatus.OVERDUE);
                feeRepository.save(fee);
                count++;
            }
        }
        if (count > 0) {
            logger.info("⚠️ Marked " + count + " fees as OVERDUE");
        }
    }

    // ── Financial Reports ────────────────────────────────

    public Map<String, Object> getFinancialSummary(String semester) {
        Map<String, Object> summary = new HashMap<>();

        Double totalFees = feeRepository.getTotalFeesBySemester(semester);
        Double collected = feeRepository.getTotalCollectedBySemester(semester);
        Double outstanding = feeRepository.getTotalOutstandingAmount();

        summary.put("semester", semester);
        summary.put("totalFees", totalFees != null ? totalFees : 0.0);
        summary.put("totalCollected", collected != null ? collected : 0.0);
        summary.put("totalOutstanding", outstanding != null ? outstanding : 0.0);
        summary.put("collectionRate", totalFees != null && totalFees > 0
            ? String.format("%.1f%%", (collected * 100.0 / totalFees))
            : "0%");
        summary.put("overdueCount", feeRepository.countOverdueFees());
        summary.put("reportGeneratedAt", LocalDateTime.now().toString());

        return summary;
    }
}
