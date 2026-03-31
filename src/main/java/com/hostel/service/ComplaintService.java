package com.hostel.service;

import com.hostel.model.Complaint;
import com.hostel.model.Complaint.ComplaintCategory;
import com.hostel.model.Complaint.ComplaintPriority;
import com.hostel.model.Complaint.ComplaintStatus;
import com.hostel.model.Student;
import com.hostel.repository.ComplaintRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Business logic for complaint management.
 * Handles complaint lifecycle: filing → assignment → resolution/rejection.
 */
@Service
@Transactional
public class ComplaintService {

    private static final Logger logger = Logger.getLogger(ComplaintService.class.getName());

    private final ComplaintRepository complaintRepository;
    private final StudentRepository studentRepository;

    public ComplaintService(ComplaintRepository complaintRepository,
                            StudentRepository studentRepository) {
        this.complaintRepository = complaintRepository;
        this.studentRepository = studentRepository;
    }

    // ── Complaint Filing ─────────────────────────────────

    public Complaint fileComplaint(Long studentId, String title, String description,
                                   ComplaintCategory category, ComplaintPriority priority) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        Complaint complaint = new Complaint(student, title, description, category, priority);
        Complaint saved = complaintRepository.save(complaint);

        logger.info("📝 Complaint filed: #" + saved.getId() + " - " + title +
            " [" + priority + "] by " + student.getName());

        // Auto-escalate critical complaints
        if (priority == ComplaintPriority.CRITICAL) {
            logger.warning("🚨 CRITICAL complaint filed: #" + saved.getId() + " - " + title);
        }

        return saved;
    }

    // ── Status Transitions ───────────────────────────────

    public Complaint assignComplaint(Long complaintId, String assignee) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.markInProgress(assignee);
        Complaint updated = complaintRepository.save(complaint);

        logger.info("👷 Complaint #" + complaintId + " assigned to: " + assignee);
        return updated;
    }

    public Complaint resolveComplaint(Long complaintId, String resolution) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.resolve(resolution);
        Complaint updated = complaintRepository.save(complaint);

        logger.info("✅ Complaint #" + complaintId + " RESOLVED: " + resolution);
        return updated;
    }

    public Complaint rejectComplaint(Long complaintId, String reason) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.reject(reason);
        Complaint updated = complaintRepository.save(complaint);

        logger.info("❌ Complaint #" + complaintId + " REJECTED: " + reason);
        return updated;
    }

    public Complaint escalateComplaint(Long complaintId) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.escalate();
        Complaint updated = complaintRepository.save(complaint);

        logger.warning("🚨 Complaint #" + complaintId + " ESCALATED to CRITICAL");
        return updated;
    }

    // ── Queries ──────────────────────────────────────────

    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found: " + id));
    }

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public List<Complaint> getComplaintsByStudent(Long studentId) {
        return complaintRepository.findByStudentId(studentId);
    }

    public List<Complaint> getActiveComplaints() {
        return complaintRepository.findActiveComplaints();
    }

    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status);
    }

    public List<Complaint> getComplaintsByCategory(ComplaintCategory category) {
        return complaintRepository.findByCategory(category);
    }

    public List<Complaint> getEscalatedComplaints() {
        return complaintRepository.findEscalatedComplaints();
    }

    // ── Statistics ────────────────────────────────────────

    public Map<String, Object> getComplaintStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalComplaints", complaintRepository.count());
        stats.put("openComplaints", complaintRepository.countOpenComplaints());
        stats.put("resolvedComplaints", complaintRepository.countResolvedComplaints());

        long total = complaintRepository.count();
        long resolved = complaintRepository.countResolvedComplaints();
        stats.put("resolutionRate", total > 0
            ? String.format("%.1f%%", (resolved * 100.0 / total))
            : "0%");

        // Category breakdown
        List<Object[]> categoryBreakdown = complaintRepository.getComplaintCountByCategory();
        Map<String, Long> categories = new HashMap<>();
        for (Object[] row : categoryBreakdown) {
            categories.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("categoryBreakdown", categories);

        return stats;
    }
}
