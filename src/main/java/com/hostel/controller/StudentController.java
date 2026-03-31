package com.hostel.controller;

import com.hostel.model.Student;
import com.hostel.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for student management operations.
 * Provides endpoints for CRUD, search, and student statistics.
 */
@RestController
@RequestMapping("/api/students")
@CrossOrigin(origins = "*")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ── POST Endpoints ───────────────────────────────────

    @PostMapping
    public ResponseEntity<?> registerStudent(@Valid @RequestBody Student student) {
        try {
            Student saved = studentService.registerStudent(student);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── GET Endpoints ────────────────────────────────────

    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(studentService.getStudentById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/enrollment/{enrollmentNumber}")
    public ResponseEntity<?> getStudentByEnrollment(@PathVariable String enrollmentNumber) {
        try {
            return ResponseEntity.ok(studentService.getStudentByEnrollment(enrollmentNumber));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Student>> searchStudents(@RequestParam String keyword) {
        return ResponseEntity.ok(studentService.searchStudents(keyword));
    }

    @GetMapping("/department/{department}")
    public ResponseEntity<List<Student>> getByDepartment(@PathVariable String department) {
        return ResponseEntity.ok(studentService.getStudentsByDepartment(department));
    }

    @GetMapping("/year/{year}")
    public ResponseEntity<List<Student>> getByYear(@PathVariable Integer year) {
        return ResponseEntity.ok(studentService.getStudentsByYear(year));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<Student>> getUnassignedStudents() {
        return ResponseEntity.ok(studentService.getUnassignedStudents());
    }

    @GetMapping("/active")
    public ResponseEntity<List<Student>> getActiveStudents() {
        return ResponseEntity.ok(studentService.getActiveStudents());
    }

    @GetMapping("/mess")
    public ResponseEntity<List<Student>> getMessStudents() {
        return ResponseEntity.ok(studentService.getMessStudents());
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStudentStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", studentService.getAllStudents().size());
        stats.put("activeStudents", studentService.getActiveStudentCount());
        stats.put("unassignedStudents", studentService.getUnassignedStudentCount());
        return ResponseEntity.ok(stats);
    }

    // ── PUT Endpoints ────────────────────────────────────

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @Valid @RequestBody Student student) {
        try {
            return ResponseEntity.ok(studentService.updateStudent(id, student));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── DELETE / Status Endpoints ─────────────────────────

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateStudent(@PathVariable Long id) {
        try {
            studentService.deactivateStudent(id);
            return ResponseEntity.ok(Map.of("message", "Student deactivated successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }
}
