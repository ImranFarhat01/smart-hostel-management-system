package com.hostel.repository;

import com.hostel.model.Complaint;
import com.hostel.model.Complaint.ComplaintCategory;
import com.hostel.model.Complaint.ComplaintPriority;
import com.hostel.model.Complaint.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Data access layer for Complaint entity.
 * Provides queries for complaint tracking and resolution management.
 */
@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {

    List<Complaint> findByStudentId(Long studentId);

    List<Complaint> findByStatus(ComplaintStatus status);

    List<Complaint> findByCategory(ComplaintCategory category);

    List<Complaint> findByPriority(ComplaintPriority priority);

    List<Complaint> findByAssignedTo(String assignedTo);

    @Query("SELECT c FROM Complaint c WHERE c.status IN ('OPEN', 'IN_PROGRESS', 'ESCALATED') ORDER BY c.priority DESC, c.createdAt ASC")
    List<Complaint> findActiveComplaints();

    @Query("SELECT c FROM Complaint c WHERE c.status = 'ESCALATED' ORDER BY c.createdAt ASC")
    List<Complaint> findEscalatedComplaints();

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = 'OPEN'")
    long countOpenComplaints();

    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = 'RESOLVED'")
    long countResolvedComplaints();

    @Query("SELECT c.category, COUNT(c) FROM Complaint c GROUP BY c.category ORDER BY COUNT(c) DESC")
    List<Object[]> getComplaintCountByCategory();
}
