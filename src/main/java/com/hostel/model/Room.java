package com.hostel.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a hostel room.
 * Tracks capacity, occupancy, type, and assigned students.
 */
@Entity
@Table(name = "rooms", indexes = {
    @Index(name = "idx_room_number", columnList = "roomNumber", unique = true),
    @Index(name = "idx_room_floor", columnList = "floor"),
    @Index(name = "idx_room_status", columnList = "status")
})
@EntityListeners(AuditingEntityListener.class)
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Room number is required")
    @Column(nullable = false, unique = true, length = 10)
    private String roomNumber;

    @NotNull(message = "Floor is required")
    @Min(value = 0, message = "Floor must be 0 or higher")
    @Max(value = 20, message = "Floor cannot exceed 20")
    @Column(nullable = false)
    private Integer floor;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    @Max(value = 8, message = "Capacity cannot exceed 8")
    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Integer occupied = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomType type = RoomType.STANDARD;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RoomStatus status = RoomStatus.AVAILABLE;

    @Column(name = "has_ac", nullable = false)
    private Boolean hasAC = false;

    @Column(nullable = false)
    private Boolean hasWifi = true;

    @Column(nullable = false)
    private Boolean hasAttachedBathroom = false;

    @Column(nullable = false)
    private Double monthlyRent = 0.0;


    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties({"room"})
    private List<Student> students = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // ── Enums ─────────────────────────────────────────────
    public enum RoomType {
        STANDARD, DELUXE, SUITE, DORMITORY
    }

    public enum RoomStatus {
        AVAILABLE, FULL, MAINTENANCE, RESERVED
    }

    // ── Business Logic ───────────────────────────────────
    public boolean hasVacancy() {
        return this.occupied < this.capacity && this.status == RoomStatus.AVAILABLE;
    }

    public int getAvailableBeds() {
        return this.capacity - this.occupied;
    }

    public void incrementOccupancy() {
        if (!hasVacancy()) {
            throw new IllegalStateException("Room " + roomNumber + " is full. Cannot add more students.");
        }
        this.occupied++;
        if (this.occupied >= this.capacity) {
            this.status = RoomStatus.FULL;
        }
    }

    public void decrementOccupancy() {
        if (this.occupied <= 0) {
            throw new IllegalStateException("Room " + roomNumber + " is already empty.");
        }
        this.occupied--;
        if (this.status == RoomStatus.FULL) {
            this.status = RoomStatus.AVAILABLE;
        }
    }

    // ── Constructors ─────────────────────────────────────
    public Room() {}

    public Room(String roomNumber, Integer floor, Integer capacity, RoomType type, Double monthlyRent) {
        this.roomNumber = roomNumber;
        this.floor = floor;
        this.capacity = capacity;
        this.type = type;
        this.monthlyRent = monthlyRent;
    }

    // ── Getters and Setters ──────────────────────────────
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public Integer getOccupied() { return occupied; }
    public void setOccupied(Integer occupied) { this.occupied = occupied; }

    public RoomType getType() { return type; }
    public void setType(RoomType type) { this.type = type; }

    public RoomStatus getStatus() { return status; }
    public void setStatus(RoomStatus status) { this.status = status; }

    public Boolean getHasAC() { return hasAC; }
    public void setHasAC(Boolean hasAC) { this.hasAC = hasAC; }

    public Boolean getHasWifi() { return hasWifi; }
    public void setHasWifi(Boolean hasWifi) { this.hasWifi = hasWifi; }

    public Boolean getHasAttachedBathroom() { return hasAttachedBathroom; }
    public void setHasAttachedBathroom(Boolean hasAttachedBathroom) { this.hasAttachedBathroom = hasAttachedBathroom; }

    public Double getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(Double monthlyRent) { this.monthlyRent = monthlyRent; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Room{number='" + roomNumber + "', floor=" + floor +
               ", occupied=" + occupied + "/" + capacity + ", status=" + status + "}";
    }
}
