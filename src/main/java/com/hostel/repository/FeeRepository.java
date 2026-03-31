package com.hostel.repository;

import com.hostel.model.Fee;
import com.hostel.model.Fee.FeeType;
import com.hostel.model.Fee.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Data access layer for Fee entity.
 * Provides queries for payment tracking and financial reporting.
 */
@Repository
public interface FeeRepository extends JpaRepository<Fee, Long> {

    List<Fee> findByStudentId(Long studentId);

    List<Fee> findByPaymentStatus(PaymentStatus status);

    List<Fee> findByFeeType(FeeType feeType);

    List<Fee> findBySemester(String semester);

    @Query("SELECT f FROM Fee f WHERE f.paymentStatus IN ('PENDING', 'PARTIAL') AND f.dueDate < :today")
    List<Fee> findOverdueFees(@Param("today") LocalDate today);

    @Query("SELECT f FROM Fee f WHERE f.student.id = :studentId AND f.paymentStatus IN ('PENDING', 'PARTIAL', 'OVERDUE')")
    List<Fee> findPendingFeesByStudent(@Param("studentId") Long studentId);

    @Query("SELECT SUM(f.totalAmount) FROM Fee f WHERE f.semester = :semester")
    Double getTotalFeesBySemester(@Param("semester") String semester);

    @Query("SELECT SUM(f.paidAmount) FROM Fee f WHERE f.semester = :semester")
    Double getTotalCollectedBySemester(@Param("semester") String semester);

    @Query("SELECT SUM(f.totalAmount - f.paidAmount) FROM Fee f WHERE f.paymentStatus IN ('PENDING', 'PARTIAL', 'OVERDUE')")
    Double getTotalOutstandingAmount();

    @Query("SELECT COUNT(f) FROM Fee f WHERE f.paymentStatus = 'OVERDUE'")
    long countOverdueFees();
}
