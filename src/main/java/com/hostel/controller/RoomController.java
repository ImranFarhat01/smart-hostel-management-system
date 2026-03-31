package com.hostel.controller;

import com.hostel.model.Room;
import com.hostel.model.Room.RoomType;
import com.hostel.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for room management and allocation operations.
 */
@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // ── Room CRUD ────────────────────────────────────────

    @PostMapping
    public ResponseEntity<?> addRoom(@Valid @RequestBody Room room) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(roomService.addRoom(room));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {
        return ResponseEntity.ok(roomService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoomById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(roomService.getRoomById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/number/{roomNumber}")
    public ResponseEntity<?> getRoomByNumber(@PathVariable String roomNumber) {
        try {
            return ResponseEntity.ok(roomService.getRoomByNumber(roomNumber));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoom(@PathVariable Long id, @Valid @RequestBody Room room) {
        try {
            return ResponseEntity.ok(roomService.updateRoom(id, room));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ── Allocation Endpoints ─────────────────────────────

    @PostMapping("/assign")
    public ResponseEntity<?> assignStudentToRoom(@RequestParam Long studentId, @RequestParam Long roomId) {
        try {
            Room updated = roomService.assignStudentToRoom(studentId, roomId);
            return ResponseEntity.ok(Map.of(
                "message", "Student assigned successfully",
                "room", updated.getRoomNumber(),
                "occupancy", updated.getOccupied() + "/" + updated.getCapacity()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/unassign")
    public ResponseEntity<?> unassignStudent(@RequestParam Long studentId) {
        try {
            roomService.unassignStudent(studentId);
            return ResponseEntity.ok(Map.of("message", "Student unassigned successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transferStudent(@RequestParam Long studentId, @RequestParam Long newRoomId) {
        try {
            Room newRoom = roomService.transferStudent(studentId, newRoomId);
            return ResponseEntity.ok(Map.of(
                "message", "Student transferred successfully",
                "newRoom", newRoom.getRoomNumber()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Availability Endpoints ───────────────────────────

    @GetMapping("/available")
    public ResponseEntity<List<Room>> getAvailableRooms() {
        return ResponseEntity.ok(roomService.getAvailableRooms());
    }

    @GetMapping("/available/type/{type}")
    public ResponseEntity<List<Room>> getAvailableByType(@PathVariable RoomType type) {
        return ResponseEntity.ok(roomService.getAvailableRoomsByType(type));
    }

    @GetMapping("/available/floor/{floor}")
    public ResponseEntity<List<Room>> getAvailableByFloor(@PathVariable Integer floor) {
        return ResponseEntity.ok(roomService.getAvailableRoomsByFloor(floor));
    }

    @GetMapping("/floor/{floor}")
    public ResponseEntity<List<Room>> getRoomsByFloor(@PathVariable Integer floor) {
        return ResponseEntity.ok(roomService.getRoomsByFloor(floor));
    }

    // ── Maintenance ──────────────────────────────────────

    @PatchMapping("/{id}/maintenance")
    public ResponseEntity<?> setMaintenance(@PathVariable Long id) {
        try {
            roomService.setRoomMaintenance(id);
            return ResponseEntity.ok(Map.of("message", "Room set to maintenance mode"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ── Statistics ────────────────────────────────────────

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getRoomStats() {
        return ResponseEntity.ok(roomService.getRoomStatistics());
    }
}
