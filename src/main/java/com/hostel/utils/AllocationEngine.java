package com.hostel.utils;

import com.hostel.model.Room;
import com.hostel.model.Room.RoomType;
import com.hostel.model.Student;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Smart Room Allocation Engine.
 * 
 * Implements intelligent allocation strategies:
 * 1. FIRST_FIT     — Assigns to the first available room
 * 2. BEST_FIT      — Assigns to room with least available space (minimizes waste)
 * 3. DEPARTMENT     — Groups students by department for better community
 * 4. YEAR_BASED    — Groups students by year of study
 * 
 * This engine can be extended with ML-based preferences in future iterations.
 */
public class AllocationEngine {

    private static final Logger logger = Logger.getLogger(AllocationEngine.class.getName());

    public enum AllocationStrategy {
        FIRST_FIT, BEST_FIT, DEPARTMENT_GROUPING, YEAR_BASED
    }

    /**
     * Finds the best room for a student based on the chosen strategy.
     *
     * @param student    The student to allocate
     * @param rooms      List of all rooms
     * @param strategy   Allocation strategy to use
     * @return           The recommended room, or empty if none available
     */
    public static Optional<Room> findBestRoom(Student student, List<Room> rooms,
                                                AllocationStrategy strategy) {
        // Filter to only available rooms
        List<Room> available = rooms.stream()
                .filter(Room::hasVacancy)
                .collect(Collectors.toList());

        if (available.isEmpty()) {
            logger.warning("⚠️ No available rooms for allocation");
            return Optional.empty();
        }

        return switch (strategy) {
            case FIRST_FIT -> firstFit(available);
            case BEST_FIT -> bestFit(available);
            case DEPARTMENT_GROUPING -> departmentGrouping(student, available);
            case YEAR_BASED -> yearBasedAllocation(student, available);
        };
    }

    /**
     * FIRST_FIT: Returns the first room with vacancy.
     * Simple, fast — O(1) in best case.
     */
    private static Optional<Room> firstFit(List<Room> availableRooms) {
        logger.info("🔍 Strategy: FIRST_FIT");
        return availableRooms.stream().findFirst();
    }

    /**
     * BEST_FIT: Returns the room with the least remaining space.
     * Minimizes wasted beds — maximizes room utilization.
     */
    private static Optional<Room> bestFit(List<Room> availableRooms) {
        logger.info("🔍 Strategy: BEST_FIT");
        return availableRooms.stream()
                .min(Comparator.comparingInt(Room::getAvailableBeds));
    }

    /**
     * DEPARTMENT_GROUPING: Tries to place student with same-department peers.
     * Promotes academic community. Falls back to BEST_FIT if no match.
     */
    private static Optional<Room> departmentGrouping(Student student, List<Room> availableRooms) {
        logger.info("🔍 Strategy: DEPARTMENT_GROUPING for dept=" + student.getDepartment());

        // Find rooms that already have students from the same department
        Optional<Room> deptRoom = availableRooms.stream()
                .filter(room -> room.getStudents().stream()
                        .anyMatch(s -> s.getDepartment().equals(student.getDepartment())))
                .min(Comparator.comparingInt(Room::getAvailableBeds));

        if (deptRoom.isPresent()) {
            logger.info("✅ Found department-matching room: " + deptRoom.get().getRoomNumber());
            return deptRoom;
        }

        // Fallback to best-fit
        logger.info("↩️ No department match found, falling back to BEST_FIT");
        return bestFit(availableRooms);
    }

    /**
     * YEAR_BASED: Groups students by year of study.
     * Freshers with freshers, seniors with seniors. Falls back to BEST_FIT.
     */
    private static Optional<Room> yearBasedAllocation(Student student, List<Room> availableRooms) {
        logger.info("🔍 Strategy: YEAR_BASED for year=" + student.getYear());

        Optional<Room> yearRoom = availableRooms.stream()
                .filter(room -> room.getStudents().stream()
                        .anyMatch(s -> s.getYear().equals(student.getYear())))
                .min(Comparator.comparingInt(Room::getAvailableBeds));

        if (yearRoom.isPresent()) {
            logger.info("✅ Found year-matching room: " + yearRoom.get().getRoomNumber());
            return yearRoom;
        }

        logger.info("↩️ No year match found, falling back to BEST_FIT");
        return bestFit(availableRooms);
    }

    /**
     * Bulk allocation: assigns a list of unassigned students to available rooms.
     * Returns a map of student → assigned room.
     */
    public static Map<Student, Room> bulkAllocate(List<Student> students, List<Room> rooms,
                                                    AllocationStrategy strategy) {
        Map<Student, Room> allocations = new LinkedHashMap<>();

        for (Student student : students) {
            if (student.getRoom() != null) continue; // Skip already assigned

            Optional<Room> room = findBestRoom(student, rooms, strategy);
            if (room.isPresent()) {
                Room r = room.get();
                student.setRoom(r);
                r.incrementOccupancy();
                allocations.put(student, r);
                logger.info("📌 Bulk allocated: " + student.getName() + " → Room " + r.getRoomNumber());
            } else {
                logger.warning("⚠️ Could not allocate room for: " + student.getName());
            }
        }

        logger.info("📊 Bulk allocation complete: " + allocations.size() + "/" + students.size() + " students allocated");
        return allocations;
    }

    /**
     * Generates an allocation report showing room utilization.
     */
    public static Map<String, Object> generateAllocationReport(List<Room> rooms) {
        Map<String, Object> report = new LinkedHashMap<>();

        int totalCapacity = rooms.stream().mapToInt(Room::getCapacity).sum();
        int totalOccupied = rooms.stream().mapToInt(Room::getOccupied).sum();
        long fullRooms = rooms.stream().filter(r -> !r.hasVacancy()).count();
        long emptyRooms = rooms.stream().filter(r -> r.getOccupied() == 0).count();

        report.put("totalRooms", rooms.size());
        report.put("totalCapacity", totalCapacity);
        report.put("totalOccupied", totalOccupied);
        report.put("availableBeds", totalCapacity - totalOccupied);
        report.put("fullRooms", fullRooms);
        report.put("emptyRooms", emptyRooms);
        report.put("occupancyRate", totalCapacity > 0
            ? String.format("%.1f%%", (totalOccupied * 100.0 / totalCapacity))
            : "0%");

        // Breakdown by room type
        Map<RoomType, Long> typeBreakdown = rooms.stream()
                .collect(Collectors.groupingBy(Room::getType, Collectors.counting()));
        report.put("roomTypeBreakdown", typeBreakdown);

        // Floor-wise occupancy
        Map<Integer, String> floorOccupancy = rooms.stream()
                .collect(Collectors.groupingBy(Room::getFloor,
                    Collectors.collectingAndThen(Collectors.toList(), floorRooms -> {
                        int cap = floorRooms.stream().mapToInt(Room::getCapacity).sum();
                        int occ = floorRooms.stream().mapToInt(Room::getOccupied).sum();
                        return occ + "/" + cap;
                    })));
        report.put("floorOccupancy", floorOccupancy);

        return report;
    }
}
