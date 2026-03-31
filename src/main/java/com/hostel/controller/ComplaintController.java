package com.hostel.controller;

import com.hostel.model.Complaint;
import com.hostel.model.Complaint.ComplaintCategory;
import com.hostel.model.Complaint.ComplaintPriority;
import com.hostel.model.Complaint.ComplaintStatus;
import com.hostel.service.ComplaintService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for complaint management and tracking.
 */
@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    // ── Filing ───────────────────────────────────────────

    @PostMapping
    public ResponseEntity<?> fileComplaint(
            @RequestParam Long studentId,
            @RequestParam String title,
            @RequestParam String description,
            @RequestParam ComplaintCategory category,
            @RequestParam(defaultValue = "MEDIUM") ComplaintPriority priority) {
        try {
            Complaint complaint = complaintService.fileComplaint(
                studentId, title, description, category, priority);
            return ResponseEntity.status(HttpStatus.CREATED).body(complaint);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Status Transitions ───────────────────────────────

    @PatchMapping("/{id}/assign")
    public ResponseEntity<?> assignComplaint(
            @PathVariable Long id, @RequestParam String assignee) {
        try {
            return ResponseEntity.ok(complaintService.assignComplaint(id, assignee));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<?> resolveComplaint(
            @PathVariable Long id, @RequestParam String resolution) {
        try {
            Complaint resolved = complaintService.resolveComplaint(id, resolution);
            return ResponseEntity.ok(Map.of(
                "message", "Complaint resolved successfully",
                "complaintId", resolved.getId(),
                "status", resolved.getStatus().toString(),
                "resolution", resolved.getResolution()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<?> rejectComplaint(
            @PathVariable Long id, @RequestParam String reason) {
        try {
            return ResponseEntity.ok(complaintService.rejectComplaint(id, reason));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PatchMapping("/{id}/escalate")
    public ResponseEntity<?> escalateComplaint(@PathVariable Long id) {
        try {
            Complaint escalated = complaintService.escalateComplaint(id);
            return ResponseEntity.ok(Map.of(
                "message", "Complaint escalated to CRITICAL",
                "complaintId", escalated.getId(),
                "priority", escalated.getPriority().toString()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Query Endpoints ──────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Complaint>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getComplaintById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(complaintService.getComplaintById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Complaint>> getComplaintsByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(complaintService.getComplaintsByStudent(studentId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Complaint>> getActiveComplaints() {
        return ResponseEntity.ok(complaintService.getActiveComplaints());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Complaint>> getByStatus(@PathVariable ComplaintStatus status) {
        return ResponseEntity.ok(complaintService.getComplaintsByStatus(status));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Complaint>> getByCategory(@PathVariable ComplaintCategory category) {
        return ResponseEntity.ok(complaintService.getComplaintsByCategory(category));
    }

    @GetMapping("/escalated")
    public ResponseEntity<List<Complaint>> getEscalatedComplaints() {
        return ResponseEntity.ok(complaintService.getEscalatedComplaints());
    }

    // ── Statistics ────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getComplaintStats() {
        return ResponseEntity.ok(complaintService.getComplaintStatistics());
    }
}
