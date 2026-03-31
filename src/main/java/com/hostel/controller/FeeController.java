package com.hostel.controller;

import com.hostel.model.Fee;
import com.hostel.model.Fee.FeeType;
import com.hostel.model.Fee.PaymentMethod;
import com.hostel.model.Fee.PaymentStatus;
import com.hostel.service.FeeService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST controller for fee management and payment processing.
 */
@RestController
@RequestMapping("/api/fees")
@CrossOrigin(origins = "*")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    // ── Fee Creation ─────────────────────────────────────

    @PostMapping
    public ResponseEntity<?> createFee(
            @RequestParam Long studentId,
            @RequestParam Double totalAmount,
            @RequestParam FeeType feeType,
            @RequestParam String semester,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        try {
            Fee fee = feeService.createFee(studentId, totalAmount, feeType, semester, dueDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(fee);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/bulk")
    public ResponseEntity<?> generateBulkFees(
            @RequestParam Double amount,
            @RequestParam FeeType feeType,
            @RequestParam String semester,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        try {
            List<Fee> fees = feeService.generateBulkFees(amount, feeType, semester, dueDate);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Bulk fees generated successfully",
                "count", fees.size(),
                "semester", semester,
                "amount", amount
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Payment Processing ───────────────────────────────

    @PostMapping("/{feeId}/pay")
    public ResponseEntity<?> makePayment(
            @PathVariable Long feeId,
            @RequestParam Double amount,
            @RequestParam PaymentMethod method,
            @RequestParam(required = false) String transactionId) {
        try {
            Fee updated = feeService.makePayment(feeId, amount, method,
                transactionId != null ? transactionId : "TXN-" + System.currentTimeMillis());
            return ResponseEntity.ok(Map.of(
                "message", "Payment processed successfully",
                "feeId", updated.getId(),
                "paidAmount", updated.getPaidAmount(),
                "balance", updated.getBalanceAmount(),
                "status", updated.getPaymentStatus().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{feeId}/waive")
    public ResponseEntity<?> waiveFee(@PathVariable Long feeId, @RequestParam String reason) {
        try {
            Fee waived = feeService.waiveFee(feeId, reason);
            return ResponseEntity.ok(Map.of(
                "message", "Fee waived successfully",
                "feeId", waived.getId(),
                "reason", reason
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Query Endpoints ──────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Fee>> getAllFees() {
        return ResponseEntity.ok(feeService.getAllFees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFeeById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(feeService.getFeeById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Fee>> getFeesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(feeService.getFeesByStudent(studentId));
    }

    @GetMapping("/student/{studentId}/pending")
    public ResponseEntity<List<Fee>> getPendingFees(@PathVariable Long studentId) {
        return ResponseEntity.ok(feeService.getPendingFees(studentId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<Fee>> getOverdueFees() {
        return ResponseEntity.ok(feeService.getOverdueFees());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Fee>> getFeesByStatus(@PathVariable PaymentStatus status) {
        return ResponseEntity.ok(feeService.getFeesByStatus(status));
    }

    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<Fee>> getFeesBySemester(@PathVariable String semester) {
        return ResponseEntity.ok(feeService.getFeesBySemester(semester));
    }

    // ── Reports ──────────────────────────────────────────

    @GetMapping("/report/{semester}")
    public ResponseEntity<Map<String, Object>> getFinancialReport(@PathVariable String semester) {
        return ResponseEntity.ok(feeService.getFinancialSummary(semester));
    }

    @PostMapping("/refresh-overdue")
    public ResponseEntity<?> refreshOverdueStatuses() {
        feeService.refreshOverdueStatuses();
        return ResponseEntity.ok(Map.of("message", "Overdue statuses refreshed"));
    }
}
