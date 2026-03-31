package com.hostel.service;

import com.hostel.model.Room;
import com.hostel.model.Room.RoomStatus;
import com.hostel.model.Room.RoomType;
import com.hostel.model.Student;
import com.hostel.repository.RoomRepository;
import com.hostel.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Business logic for room management and student-room allocation.
 * Implements smart allocation engine with capacity validation.
 */
@Service
@Transactional
public class RoomService {

    private static final Logger logger = Logger.getLogger(RoomService.class.getName());

    private final RoomRepository roomRepository;
    private final StudentRepository studentRepository;

    public RoomService(RoomRepository roomRepository, StudentRepository studentRepository) {
        this.roomRepository = roomRepository;
        this.studentRepository = studentRepository;
    }

    // ── Room CRUD ────────────────────────────────────────

    public Room addRoom(Room room) {
        if (roomRepository.existsByRoomNumber(room.getRoomNumber())) {
            throw new IllegalArgumentException("Room number already exists: " + room.getRoomNumber());
        }
        Room saved = roomRepository.save(room);
        logger.info("🏠 Room added: " + saved.getRoomNumber() + " (capacity: " + saved.getCapacity() + ")");
        return saved;
    }

    public Room getRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Room not found with ID: " + id));
    }

    public Room getRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new RuntimeException("Room not found: " + roomNumber));
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room updateRoom(Long id, Room updatedData) {
        Room existing = getRoomById(id);
        existing.setType(updatedData.getType());
        existing.setHasAC(updatedData.getHasAC());
        existing.setHasWifi(updatedData.getHasWifi());
        existing.setHasAttachedBathroom(updatedData.getHasAttachedBathroom());
        existing.setMonthlyRent(updatedData.getMonthlyRent());
        return roomRepository.save(existing);
    }

    public void setRoomMaintenance(Long roomId) {
        Room room = getRoomById(roomId);
        if (room.getOccupied() > 0) {
            throw new IllegalStateException("Cannot set maintenance on occupied room " + room.getRoomNumber());
        }
        room.setStatus(RoomStatus.MAINTENANCE);
        roomRepository.save(room);
        logger.info("🔧 Room " + room.getRoomNumber() + " set to MAINTENANCE");
    }

    // ── Room Allocation ──────────────────────────────────

    /**
     * Core allocation logic: assigns a student to a room.
     * Validates capacity, student status, and prevents duplicate assignments.
     */
    public Room assignStudentToRoom(Long studentId, Long roomId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        Room room = getRoomById(roomId);

        // Validation checks
        if (student.getRoom() != null) {
            throw new IllegalStateException("Student " + student.getName() +
                " is already assigned to room " + student.getRoom().getRoomNumber() +
                ". Unassign first.");
        }

        if (!room.hasVacancy()) {
            throw new IllegalStateException("Room " + room.getRoomNumber() +
                " is full (" + room.getOccupied() + "/" + room.getCapacity() + ")");
        }

        if (student.getStatus() != Student.StudentStatus.ACTIVE) {
            throw new IllegalStateException("Cannot assign inactive student to a room");
        }

        // Perform allocation
        student.setRoom(room);
        room.incrementOccupancy();

        studentRepository.save(student);
        roomRepository.save(room);

        logger.info("✅ Allocated: " + student.getName() + " → Room " + room.getRoomNumber() +
            " (" + room.getOccupied() + "/" + room.getCapacity() + ")");

        return room;
    }

    /**
     * Unassigns a student from their current room.
     */
    public void unassignStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));

        if (student.getRoom() == null) {
            throw new IllegalStateException("Student " + student.getName() + " is not assigned to any room");
        }

        Room room = student.getRoom();
        room.decrementOccupancy();
        student.setRoom(null);

        studentRepository.save(student);
        roomRepository.save(room);

        logger.info("🔄 Unassigned: " + student.getName() + " from Room " + room.getRoomNumber());
    }

    /**
     * Transfers a student from one room to another.
     */
    public Room transferStudent(Long studentId, Long newRoomId) {
        unassignStudent(studentId);
        return assignStudentToRoom(studentId, newRoomId);
    }

    // ── Availability Queries ─────────────────────────────

    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailableRooms();
    }

    public List<Room> getAvailableRoomsByType(RoomType type) {
        return roomRepository.findAvailableRoomsByType(type);
    }

    public List<Room> getAvailableRoomsByFloor(Integer floor) {
        return roomRepository.findAvailableRoomsByFloor(floor);
    }

    public List<Room> getRoomsByFloor(Integer floor) {
        return roomRepository.findByFloor(floor);
    }

    // ── Statistics ────────────────────────────────────────

    public Map<String, Object> getRoomStatistics() {
        Map<String, Object> stats = new HashMap<>();
        Long totalCapacity = roomRepository.getTotalCapacity();
        Long totalOccupied = roomRepository.getTotalOccupied();

        stats.put("totalRooms", roomRepository.count());
        stats.put("totalCapacity", totalCapacity != null ? totalCapacity : 0);
        stats.put("totalOccupied", totalOccupied != null ? totalOccupied : 0);
        stats.put("availableRooms", roomRepository.countAvailableRooms());
        stats.put("fullRooms", roomRepository.countFullRooms());
        stats.put("occupancyRate", totalCapacity != null && totalCapacity > 0
            ? String.format("%.1f%%", (totalOccupied * 100.0 / totalCapacity))
            : "0%");

        return stats;
    }
}
