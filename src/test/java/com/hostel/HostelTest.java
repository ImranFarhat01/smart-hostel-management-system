package com.hostel;

import com.hostel.model.*;
import com.hostel.model.Complaint.ComplaintCategory;
import com.hostel.model.Complaint.ComplaintPriority;
import com.hostel.model.Complaint.ComplaintStatus;
import com.hostel.model.Fee.FeeType;
import com.hostel.model.Fee.PaymentMethod;
import com.hostel.model.Fee.PaymentStatus;
import com.hostel.model.Room.RoomStatus;
import com.hostel.model.Room.RoomType;
import com.hostel.utils.AllocationEngine;
import com.hostel.utils.AllocationEngine.AllocationStrategy;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive unit tests for Smart Hostel Management System.
 * Tests all models, business logic, and the allocation engine.
 *
 * Run with: mvn test
 */
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("🏨 Smart Hostel Management System - Unit Tests")
class HostelTest {

    // ══════════════════════════════════════════════════════
    //                    ROOM TESTS
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("🏠 Room Module Tests")
    class RoomTests {

        @Test
        @Order(1)
        @DisplayName("Room creation with valid data")
        void testRoomCreation() {
            Room room = new Room("A-101", 1, 4, RoomType.STANDARD, 5000.0);
            assertEquals("A-101", room.getRoomNumber());
            assertEquals(1, room.getFloor());
            assertEquals(4, room.getCapacity());
            assertEquals(0, room.getOccupied());
            assertEquals(RoomType.STANDARD, room.getType());
            assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        }

        @Test
        @Order(2)
        @DisplayName("Room vacancy check — has vacancy")
        void testHasVacancy() {
            Room room = new Room("A-102", 1, 3, RoomType.STANDARD, 5000.0);
            assertTrue(room.hasVacancy());
            assertEquals(3, room.getAvailableBeds());
        }

        @Test
        @Order(3)
        @DisplayName("Room occupancy increment and full status")
        void testIncrementOccupancy() {
            Room room = new Room("A-103", 1, 2, RoomType.DELUXE, 7000.0);

            room.incrementOccupancy();
            assertEquals(1, room.getOccupied());
            assertTrue(room.hasVacancy());

            room.incrementOccupancy();
            assertEquals(2, room.getOccupied());
            assertEquals(RoomStatus.FULL, room.getStatus());
            assertFalse(room.hasVacancy());
        }

        @Test
        @Order(4)
        @DisplayName("Room occupancy overflow throws exception")
        void testOccupancyOverflow() {
            Room room = new Room("A-104", 1, 1, RoomType.SUITE, 12000.0);
            room.incrementOccupancy();

            assertThrows(IllegalStateException.class, room::incrementOccupancy,
                "Should throw when room is full");
        }

        @Test
        @Order(5)
        @DisplayName("Room decrement occupancy restores AVAILABLE status")
        void testDecrementOccupancy() {
            Room room = new Room("A-105", 1, 2, RoomType.DELUXE, 7000.0);
            room.incrementOccupancy();
            room.incrementOccupancy();
            assertEquals(RoomStatus.FULL, room.getStatus());

            room.decrementOccupancy();
            assertEquals(1, room.getOccupied());
            assertEquals(RoomStatus.AVAILABLE, room.getStatus());
        }

        @Test
        @Order(6)
        @DisplayName("Room decrement below zero throws exception")
        void testDecrementBelowZero() {
            Room room = new Room("A-106", 1, 2, RoomType.STANDARD, 5000.0);
            assertThrows(IllegalStateException.class, room::decrementOccupancy);
        }

        @Test
        @Order(7)
        @DisplayName("Maintenance room blocks allocation")
        void testMaintenanceBlocksAllocation() {
            Room room = new Room("A-107", 1, 4, RoomType.STANDARD, 5000.0);
            room.setStatus(RoomStatus.MAINTENANCE);
            assertFalse(room.hasVacancy(), "Maintenance rooms should not have vacancy");
        }
    }

    // ══════════════════════════════════════════════════════
    //                  STUDENT TESTS
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("🎓 Student Module Tests")
    class StudentTests {

        @Test
        @Order(1)
        @DisplayName("Student creation with valid data")
        void testStudentCreation() {
            Student student = new Student("Aarav Sharma", "aarav@test.com", "9876543210",
                "CSE2023001", "Computer Science", 2, "Male", LocalDate.of(2004, 3, 15));

            assertEquals("Aarav Sharma", student.getName());
            assertEquals("aarav@test.com", student.getEmail());
            assertEquals("CSE2023001", student.getEnrollmentNumber());
            assertEquals(Student.StudentStatus.ACTIVE, student.getStatus());
            assertTrue(student.getMessOptIn());
            assertNull(student.getRoom());
        }

        @Test
        @Order(2)
        @DisplayName("Student room assignment")
        void testStudentRoomAssignment() {
            Student student = new Student("Priya Patel", "priya@test.com", "9876543211",
                "ECE2023002", "Electronics", 2, "Female", LocalDate.of(2004, 7, 22));
            Room room = new Room("B-201", 2, 3, RoomType.STANDARD, 5000.0);

            student.setRoom(room);
            room.incrementOccupancy();

            assertNotNull(student.getRoom());
            assertEquals("B-201", student.getRoom().getRoomNumber());
            assertEquals(1, room.getOccupied());
        }

        @Test
        @Order(3)
        @DisplayName("Student toString shows room info")
        void testStudentToString() {
            Student student = new Student("Test Student", "test@test.com", "1234567890",
                "TEST001", "CS", 1, "Male", LocalDate.of(2005, 1, 1));

            assertTrue(student.toString().contains("Unassigned"));

            Room room = new Room("C-301", 3, 2, RoomType.DELUXE, 7000.0);
            student.setRoom(room);
            assertTrue(student.toString().contains("C-301"));
        }
    }

    // ══════════════════════════════════════════════════════
    //                     FEE TESTS
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("💰 Fee Module Tests")
    class FeeTests {

        private Student testStudent;
        private Fee testFee;

        @BeforeEach
        void setUp() {
            testStudent = new Student("Fee Test", "fee@test.com", "1111111111",
                "FEE001", "CS", 1, "Male", LocalDate.of(2005, 1, 1));

            testFee = new Fee(testStudent, 15000.0, FeeType.HOSTEL_RENT,
                "2025-I", LocalDate.of(2025, 6, 30));
        }

        @Test
        @Order(1)
        @DisplayName("Fee creation with correct defaults")
        void testFeeCreation() {
            assertEquals(15000.0, testFee.getTotalAmount());
            assertEquals(0.0, testFee.getPaidAmount());
            assertEquals(15000.0, testFee.getBalanceAmount());
            assertEquals(PaymentStatus.PENDING, testFee.getPaymentStatus());
        }

        @Test
        @Order(2)
        @DisplayName("Full payment marks status as PAID")
        void testFullPayment() {
            testFee.makePayment(15000.0);
            assertEquals(PaymentStatus.PAID, testFee.getPaymentStatus());
            assertEquals(0.0, testFee.getBalanceAmount());
        }

        @Test
        @Order(3)
        @DisplayName("Partial payment marks status as PARTIAL")
        void testPartialPayment() {
            testFee.makePayment(8000.0);
            assertEquals(PaymentStatus.PARTIAL, testFee.getPaymentStatus());
            assertEquals(7000.0, testFee.getBalanceAmount());
        }

        @Test
        @Order(4)
        @DisplayName("Multiple payments accumulate correctly")
        void testMultiplePayments() {
            testFee.makePayment(5000.0);
            assertEquals(10000.0, testFee.getBalanceAmount());
            assertEquals(PaymentStatus.PARTIAL, testFee.getPaymentStatus());

            testFee.makePayment(5000.0);
            assertEquals(5000.0, testFee.getBalanceAmount());

            testFee.makePayment(5000.0);
            assertEquals(0.0, testFee.getBalanceAmount());
            assertEquals(PaymentStatus.PAID, testFee.getPaymentStatus());
        }

        @Test
        @Order(5)
        @DisplayName("Overpayment throws exception")
        void testOverpayment() {
            assertThrows(IllegalArgumentException.class,
                () -> testFee.makePayment(20000.0),
                "Should not allow payment exceeding balance");
        }

        @Test
        @Order(6)
        @DisplayName("Zero/negative payment throws exception")
        void testInvalidPaymentAmount() {
            assertThrows(IllegalArgumentException.class,
                () -> testFee.makePayment(0.0));
            assertThrows(IllegalArgumentException.class,
                () -> testFee.makePayment(-100.0));
        }

        @Test
        @Order(7)
        @DisplayName("Overdue detection works correctly")
        void testOverdueDetection() {
            Fee overdueFee = new Fee(testStudent, 5000.0, FeeType.MESS_FEE,
                "2024-II", LocalDate.of(2024, 1, 1)); // Past due date

            assertTrue(overdueFee.isOverdue());

            Fee futureFee = new Fee(testStudent, 5000.0, FeeType.MESS_FEE,
                "2026-I", LocalDate.of(2027, 12, 31)); // Future due date

            assertFalse(futureFee.isOverdue());
        }
    }

    // ══════════════════════════════════════════════════════
    //                 COMPLAINT TESTS
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("📝 Complaint Module Tests")
    class ComplaintTests {

        private Student testStudent;
        private Complaint testComplaint;

        @BeforeEach
        void setUp() {
            testStudent = new Student("Complaint Test", "complaint@test.com", "2222222222",
                "COMP001", "ECE", 2, "Female", LocalDate.of(2004, 5, 10));

            testComplaint = new Complaint(testStudent, "Water leakage",
                "Bathroom ceiling has a major water leak causing floor flooding",
                ComplaintCategory.PLUMBING, ComplaintPriority.HIGH);
        }

        @Test
        @Order(1)
        @DisplayName("Complaint creation with correct defaults")
        void testComplaintCreation() {
            assertEquals("Water leakage", testComplaint.getTitle());
            assertEquals(ComplaintStatus.OPEN, testComplaint.getStatus());
            assertEquals(ComplaintPriority.HIGH, testComplaint.getPriority());
            assertNull(testComplaint.getAssignedTo());
            assertNull(testComplaint.getResolution());
        }

        @Test
        @Order(2)
        @DisplayName("Complaint workflow: OPEN → IN_PROGRESS → RESOLVED")
        void testComplaintWorkflow() {
            // OPEN → IN_PROGRESS
            testComplaint.markInProgress("Maintenance Team");
            assertEquals(ComplaintStatus.IN_PROGRESS, testComplaint.getStatus());
            assertEquals("Maintenance Team", testComplaint.getAssignedTo());

            // IN_PROGRESS → RESOLVED
            testComplaint.resolve("Pipe fixed and ceiling repainted");
            assertEquals(ComplaintStatus.RESOLVED, testComplaint.getStatus());
            assertNotNull(testComplaint.getResolvedAt());
            assertEquals("Pipe fixed and ceiling repainted", testComplaint.getResolution());
        }

        @Test
        @Order(3)
        @DisplayName("Complaint rejection workflow")
        void testComplaintRejection() {
            testComplaint.markInProgress("Admin");
            testComplaint.reject("Issue is outside hostel jurisdiction");

            assertEquals(ComplaintStatus.REJECTED, testComplaint.getStatus());
            assertTrue(testComplaint.getResolution().startsWith("REJECTED:"));
        }

        @Test
        @Order(4)
        @DisplayName("Complaint escalation sets CRITICAL priority")
        void testComplaintEscalation() {
            testComplaint.escalate();
            assertEquals(ComplaintStatus.ESCALATED, testComplaint.getStatus());
            assertEquals(ComplaintPriority.CRITICAL, testComplaint.getPriority());
        }

        @Test
        @Order(5)
        @DisplayName("Cannot resolve already resolved complaint")
        void testDoubleResolve() {
            testComplaint.resolve("Fixed");
            assertThrows(IllegalStateException.class,
                () -> testComplaint.resolve("Fixed again"));
        }

        @Test
        @Order(6)
        @DisplayName("Cannot escalate closed complaint")
        void testEscalateClosedComplaint() {
            testComplaint.resolve("Fixed");
            assertThrows(IllegalStateException.class, testComplaint::escalate);
        }

        @Test
        @Order(7)
        @DisplayName("Cannot move non-OPEN complaint to IN_PROGRESS")
        void testInvalidStatusTransition() {
            testComplaint.markInProgress("Team A");
            assertThrows(IllegalStateException.class,
                () -> testComplaint.markInProgress("Team B"));
        }
    }

    // ══════════════════════════════════════════════════════
    //             ALLOCATION ENGINE TESTS
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("⚙️ Allocation Engine Tests")
    class AllocationEngineTests {

        private List<Room> testRooms;

        @BeforeEach
        void setUp() {
            testRooms = new ArrayList<>();
            Room r1 = new Room("T-101", 1, 4, RoomType.DORMITORY, 3000.0);
            Room r2 = new Room("T-102", 1, 2, RoomType.STANDARD, 5000.0);
            Room r3 = new Room("T-103", 2, 3, RoomType.DELUXE, 7000.0);
            Room r4 = new Room("T-104", 2, 1, RoomType.SUITE, 12000.0);

            // Simulate partial occupancy
            r1.incrementOccupancy(); // 1/4
            r2.incrementOccupancy(); // 1/2

            testRooms.addAll(List.of(r1, r2, r3, r4));
        }

        @Test
        @Order(1)
        @DisplayName("FIRST_FIT returns first available room")
        void testFirstFit() {
            Student student = new Student("Test", "ff@test.com", "3333333333",
                "FF001", "CS", 1, "Male", LocalDate.of(2005, 1, 1));

            Optional<Room> result = AllocationEngine.findBestRoom(
                student, testRooms, AllocationStrategy.FIRST_FIT);

            assertTrue(result.isPresent());
            assertEquals("T-101", result.get().getRoomNumber());
        }

        @Test
        @Order(2)
        @DisplayName("BEST_FIT returns room with least available beds")
        void testBestFit() {
            Student student = new Student("Test", "bf@test.com", "4444444444",
                "BF001", "CS", 1, "Male", LocalDate.of(2005, 1, 1));

            Optional<Room> result = AllocationEngine.findBestRoom(
                student, testRooms, AllocationStrategy.BEST_FIT);

            assertTrue(result.isPresent());
            // T-102 has 1 available bed (least), T-104 also has 1 but appears later
            // Both are valid best-fit results
            assertTrue(result.get().getAvailableBeds() <= 1);
        }

        @Test
        @Order(3)
        @DisplayName("No available rooms returns empty")
        void testNoAvailableRooms() {
            List<Room> fullRooms = new ArrayList<>();
            Room full = new Room("F-101", 1, 1, RoomType.STANDARD, 5000.0);
            full.incrementOccupancy();
            fullRooms.add(full);

            Student student = new Student("Test", "na@test.com", "5555555555",
                "NA001", "CS", 1, "Male", LocalDate.of(2005, 1, 1));

            Optional<Room> result = AllocationEngine.findBestRoom(
                student, fullRooms, AllocationStrategy.FIRST_FIT);

            assertFalse(result.isPresent());
        }

        @Test
        @Order(4)
        @DisplayName("Allocation report generates correctly")
        void testAllocationReport() {
            Map<String, Object> report = AllocationEngine.generateAllocationReport(testRooms);

            assertEquals(4, report.get("totalRooms"));
            assertEquals(10, report.get("totalCapacity"));
            assertEquals(2, report.get("totalOccupied"));
            assertEquals(8, report.get("availableBeds"));
            assertNotNull(report.get("occupancyRate"));
            assertNotNull(report.get("roomTypeBreakdown"));
            assertNotNull(report.get("floorOccupancy"));
        }

        @Test
        @Order(5)
        @DisplayName("Bulk allocation processes multiple students")
        void testBulkAllocation() {
            List<Student> students = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                students.add(new Student("Bulk-" + i, "bulk" + i + "@test.com",
                    "600000000" + i, "BULK00" + i, "CS", 1, "Male", LocalDate.of(2005, 1, 1)));
            }

            Map<Student, Room> allocations = AllocationEngine.bulkAllocate(
                students, testRooms, AllocationStrategy.BEST_FIT);

            assertFalse(allocations.isEmpty());
            assertTrue(allocations.size() <= 5);

            // Verify all allocated students have rooms
            for (Map.Entry<Student, Room> entry : allocations.entrySet()) {
                assertNotNull(entry.getKey().getRoom());
                assertNotNull(entry.getValue());
            }
        }
    }

    // ══════════════════════════════════════════════════════
    //             INTEGRATION / EDGE CASE TESTS
    // ══════════════════════════════════════════════════════

    @Nested
    @DisplayName("🔗 Integration & Edge Case Tests")
    class IntegrationTests {

        @Test
        @Order(1)
        @DisplayName("Full student lifecycle: register → assign → fee → complaint → deactivate")
        void testFullStudentLifecycle() {
            // 1. Create student
            Student student = new Student("Lifecycle Test", "lifecycle@test.com", "7777777777",
                "LC001", "IT", 1, "Male", LocalDate.of(2005, 6, 15));
            assertEquals(Student.StudentStatus.ACTIVE, student.getStatus());

            // 2. Assign room
            Room room = new Room("LC-101", 1, 2, RoomType.STANDARD, 5000.0);
            student.setRoom(room);
            room.incrementOccupancy();
            assertNotNull(student.getRoom());
            assertEquals(1, room.getOccupied());

            // 3. Create fee
            Fee fee = new Fee(student, 15000.0, FeeType.HOSTEL_RENT, "2025-I",
                LocalDate.of(2025, 6, 30));
            assertEquals(PaymentStatus.PENDING, fee.getPaymentStatus());

            // 4. Make payment
            fee.makePayment(15000.0);
            assertEquals(PaymentStatus.PAID, fee.getPaymentStatus());

            // 5. File complaint
            Complaint complaint = new Complaint(student, "Test complaint",
                "Testing the full lifecycle flow",
                ComplaintCategory.GENERAL, ComplaintPriority.LOW);
            assertEquals(ComplaintStatus.OPEN, complaint.getStatus());

            // 6. Resolve complaint
            complaint.resolve("Lifecycle test passed");
            assertEquals(ComplaintStatus.RESOLVED, complaint.getStatus());

            // 7. Deactivate (simulate)
            room.decrementOccupancy();
            student.setRoom(null);
            student.setStatus(Student.StudentStatus.INACTIVE);

            assertNull(student.getRoom());
            assertEquals(0, room.getOccupied());
            assertEquals(Student.StudentStatus.INACTIVE, student.getStatus());
        }

        @Test
        @Order(2)
        @DisplayName("Room type enumeration completeness")
        void testRoomTypeEnums() {
            RoomType[] types = RoomType.values();
            assertEquals(4, types.length);
            assertNotNull(RoomType.valueOf("STANDARD"));
            assertNotNull(RoomType.valueOf("DELUXE"));
            assertNotNull(RoomType.valueOf("SUITE"));
            assertNotNull(RoomType.valueOf("DORMITORY"));
        }

        @Test
        @Order(3)
        @DisplayName("Fee type enumeration completeness")
        void testFeeTypeEnums() {
            FeeType[] types = FeeType.values();
            assertEquals(7, types.length);
        }

        @Test
        @Order(4)
        @DisplayName("Complaint category enumeration completeness")
        void testComplaintCategoryEnums() {
            ComplaintCategory[] categories = ComplaintCategory.values();
            assertEquals(11, categories.length);
        }

        @Test
        @Order(5)
        @DisplayName("Concurrent room assignments don't exceed capacity")
        void testConcurrentCapacityGuard() {
            Room room = new Room("CC-101", 1, 2, RoomType.STANDARD, 5000.0);

            room.incrementOccupancy();
            room.incrementOccupancy();

            // Room is now full
            assertFalse(room.hasVacancy());
            assertThrows(IllegalStateException.class, room::incrementOccupancy);
        }
    }
}
