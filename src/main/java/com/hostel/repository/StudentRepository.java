package com.hostel.repository;

import com.hostel.model.Student;
import com.hostel.model.Student.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access layer for Student entity.
 * Provides CRUD operations and custom queries for student management.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    Optional<Student> findByEnrollmentNumber(String enrollmentNumber);

    List<Student> findByStatus(StudentStatus status);

    List<Student> findByDepartment(String department);

    List<Student> findByYear(Integer year);

    List<Student> findByRoomId(Long roomId);

    List<Student> findByRoomIsNull();

    @Query("SELECT s FROM Student s WHERE s.name LIKE %:keyword% OR s.email LIKE %:keyword% OR s.enrollmentNumber LIKE %:keyword%")
    List<Student> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT s FROM Student s WHERE s.messOptIn = true AND s.status = 'ACTIVE'")
    List<Student> findActiveMessStudents();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.status = 'ACTIVE'")
    long countActiveStudents();

    @Query("SELECT COUNT(s) FROM Student s WHERE s.room IS NULL AND s.status = 'ACTIVE'")
    long countUnassignedStudents();

    boolean existsByEmail(String email);

    boolean existsByEnrollmentNumber(String enrollmentNumber);
}
