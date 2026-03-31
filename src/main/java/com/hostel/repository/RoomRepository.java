package com.hostel.repository;

import com.hostel.model.Room;
import com.hostel.model.Room.RoomStatus;
import com.hostel.model.Room.RoomType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Data access layer for Room entity.
 * Provides queries for room availability, allocation, and management.
 */
@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomNumber(String roomNumber);

    List<Room> findByStatus(RoomStatus status);

    List<Room> findByFloor(Integer floor);

    List<Room> findByType(RoomType type);

    @Query("SELECT r FROM Room r WHERE r.occupied < r.capacity AND r.status = 'AVAILABLE'")
    List<Room> findAvailableRooms();

    @Query("SELECT r FROM Room r WHERE r.occupied < r.capacity AND r.status = 'AVAILABLE' AND r.type = :type")
    List<Room> findAvailableRoomsByType(@Param("type") RoomType type);

    @Query("SELECT r FROM Room r WHERE r.occupied < r.capacity AND r.status = 'AVAILABLE' AND r.floor = :floor")
    List<Room> findAvailableRoomsByFloor(@Param("floor") Integer floor);

    @Query("SELECT SUM(r.capacity) FROM Room r")
    Long getTotalCapacity();

    @Query("SELECT SUM(r.occupied) FROM Room r")
    Long getTotalOccupied();

    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = 'AVAILABLE' AND r.occupied < r.capacity")
    long countAvailableRooms();

    @Query("SELECT COUNT(r) FROM Room r WHERE r.status = 'FULL'")
    long countFullRooms();

    boolean existsByRoomNumber(String roomNumber);
}
