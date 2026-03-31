package com.hostel.service;

import com.hostel.model.Student;
import com.hostel.model.Student.StudentStatus;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

/**
 * Business logic for student management operations.
 * Handles registration, updates, status changes, and search functionality.
 */
@Service
@Transactional
public class StudentService {

    private static final Logger logger = Logger.getLogger(StudentService.class.getName());

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    // ── CRUD Operations ──────────────────────────────────

    public Student registerStudent(Student student) {
        // Validate uniqueness
        if (studentRepository.existsByEmail(student.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + student.getEmail());
        }
        if (studentRepository.existsByEnrollmentNumber(student.getEnrollmentNumber())) {
            throw new IllegalArgumentException("Enrollment number already exists: " + student.getEnrollmentNumber());
        }

        student.setStatus(StudentStatus.ACTIVE);
        Student saved = studentRepository.save(student);
        logger.info("✅ Student registered: " + saved.getName() + " [" + saved.getEnrollmentNumber() + "]");
        return saved;
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with ID: " + id));
    }

    public Student getStudentByEnrollment(String enrollmentNumber) {
        return studentRepository.findByEnrollmentNumber(enrollmentNumber)
                .orElseThrow(() -> new RuntimeException("Student not found: " + enrollmentNumber));
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student updateStudent(Long id, Student updatedData) {
        Student existing = getStudentById(id);
        existing.setName(updatedData.getName());
        existing.setPhone(updatedData.getPhone());
        existing.setDepartment(updatedData.getDepartment());
        existing.setYear(updatedData.getYear());
        existing.setGuardianName(updatedData.getGuardianName());
        existing.setGuardianPhone(updatedData.getGuardianPhone());
        existing.setAddress(updatedData.getAddress());
        existing.setMessOptIn(updatedData.getMessOptIn());

        logger.info("📝 Student updated: " + existing.getName());
        return studentRepository.save(existing);
    }

    public void deactivateStudent(Long id) {
        Student student = getStudentById(id);
        student.setStatus(StudentStatus.INACTIVE);

        // Unassign room if assigned
        if (student.getRoom() != null) {
            student.getRoom().decrementOccupancy();
            student.setRoom(null);
        }

        studentRepository.save(student);
        logger.info("🚫 Student deactivated: " + student.getName());
    }

    // ── Query Operations ─────────────────────────────────

    public List<Student> getStudentsByDepartment(String department) {
        return studentRepository.findByDepartment(department);
    }

    public List<Student> getStudentsByYear(Integer year) {
        return studentRepository.findByYear(year);
    }

    public List<Student> getActiveStudents() {
        return studentRepository.findByStatus(StudentStatus.ACTIVE);
    }

    public List<Student> getUnassignedStudents() {
        return studentRepository.findByRoomIsNull();
    }

    public List<Student> searchStudents(String keyword) {
        return studentRepository.searchByKeyword(keyword);
    }

    public List<Student> getMessStudents() {
        return studentRepository.findActiveMessStudents();
    }

    // ── Statistics ────────────────────────────────────────

    public long getActiveStudentCount() {
        return studentRepository.countActiveStudents();
    }

    public long getUnassignedStudentCount() {
        return studentRepository.countUnassignedStudents();
    }
}
