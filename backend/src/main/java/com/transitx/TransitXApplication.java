package com.transitx;

import com.transitx.entity.*;
import com.transitx.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootApplication
public class TransitXApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransitXApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedDatabase(
            UserRepository userRepository,
            StudentRepository studentRepository,
            DriverRepository driverRepository,
            BusRepository busRepository,
            RouteRepository routeRepository,
            BusStopRepository stopRepository,
            RouteStopRepository routeStopRepository,
            BusAssignmentRepository assignmentRepository,
            BusLocationRepository locationRepository,
            StudentBusPassRepository passRepository,
            TripRepository tripRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            // Seed Admin if missing
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User(
                        "admin",
                        "admin@transitx.edu",
                        passwordEncoder.encode("admin123"),
                        Role.ROLE_ADMIN,
                        "Chief Transport Administrator",
                        "9876543210"
                );
                userRepository.save(admin);
            }

            // Seed Buses if missing
            if (busRepository.count() == 0) {
                Bus b1 = busRepository.save(new Bus("BUS-01", 52, BusStatus.ACTIVE, "KA-01-TX-1001 (Ashok Leyland)"));
                Bus b2 = busRepository.save(new Bus("BUS-02", 48, BusStatus.ACTIVE, "KA-01-TX-1002 (Tata Starbus)"));
                Bus b3 = busRepository.save(new Bus("BUS-03", 50, BusStatus.ACTIVE, "KA-01-TX-1003 (Eicher Skyline)"));
                busRepository.save(new Bus("BUS-04", 40, BusStatus.MAINTENANCE, "KA-01-TX-1004 (Tata Ultra)"));

                // Seed Drivers
                User uDriver1 = userRepository.save(new User(
                        "DRV001", "ramesh@transitx.edu", passwordEncoder.encode("driver123"),
                        Role.ROLE_DRIVER, "Ramesh Kumar", "9845123456"
                ));
                Driver d1 = driverRepository.save(new Driver(uDriver1, "DRV001", "Ramesh Kumar", "9845123456", "DL-KA-2015-008912", 9, b1));

                User uDriver2 = userRepository.save(new User(
                        "DRV002", "suresh@transitx.edu", passwordEncoder.encode("driver123"),
                        Role.ROLE_DRIVER, "Suresh Patel", "9845789123"
                ));
                Driver d2 = driverRepository.save(new Driver(uDriver2, "DRV002", "Suresh Patel", "9845789123", "DL-KA-2012-004321", 12, b2));

                // Seed Routes & Stops
                Route r1 = routeRepository.save(new Route(
                        "City Central - Campus Express", "RT-101", "Majestic Central Terminus",
                        "Main Campus Gate 1", "07:30 AM", "08:35 AM", "65 mins", new BigDecimal("24.50")
                ));

                Route r2 = routeRepository.save(new Route(
                        "Metro Corridor Link", "RT-102", "Indiranagar Metro Station",
                        "Engineering Block 3", "07:45 AM", "08:40 AM", "55 mins", new BigDecimal("18.20")
                ));

                Route r3 = routeRepository.save(new Route(
                        "Green Hills Suburb Connector", "RT-103", "Whitefield Circle",
                        "Main Campus Gate 2", "07:20 AM", "08:45 AM", "85 mins", new BigDecimal("31.00")
                ));

                // Stops for Route 1
                BusStop s1 = stopRepository.save(new BusStop("Majestic Central Terminus", new BigDecimal("12.9767500"), new BigDecimal("77.5712550"), "Platform 18 Exit"));
                BusStop s2 = stopRepository.save(new BusStop("Corporation Circle", new BigDecimal("12.9682500"), new BigDecimal("77.5891400"), "Near LIC Building"));
                BusStop s3 = stopRepository.save(new BusStop("Shantinagar Bus Station", new BigDecimal("12.9538000"), new BigDecimal("77.5957000"), "Main Depot Gate"));
                BusStop s4 = stopRepository.save(new BusStop("Dairy Circle", new BigDecimal("12.9351000"), new BigDecimal("77.6012000"), "Opposite Forum Mall Road"));
                BusStop s5 = stopRepository.save(new BusStop("Silk Board Junction", new BigDecimal("12.9174000"), new BigDecimal("77.6238000"), "Flyover Ramp Entry"));
                BusStop s6 = stopRepository.save(new BusStop("HSR Layout BDA Complex", new BigDecimal("12.9116000"), new BigDecimal("77.6389000"), "Near 14th Main Road"));
                BusStop s7 = stopRepository.save(new BusStop("Main Campus Gate 1", new BigDecimal("12.8615000"), new BigDecimal("77.6645000"), "TRANSITX Campus Bus Bay"));

                routeStopRepository.save(new RouteStop(r1, s1, 1, "07:30 AM"));
                routeStopRepository.save(new RouteStop(r1, s2, 2, "07:42 AM"));
                routeStopRepository.save(new RouteStop(r1, s3, 3, "07:54 AM"));
                routeStopRepository.save(new RouteStop(r1, s4, 4, "08:06 AM"));
                routeStopRepository.save(new RouteStop(r1, s5, 5, "08:18 AM"));
                routeStopRepository.save(new RouteStop(r1, s6, 6, "08:26 AM"));
                routeStopRepository.save(new RouteStop(r1, s7, 7, "08:35 AM"));

                // Stops for Route 2
                BusStop s8 = stopRepository.save(new BusStop("Indiranagar Metro Station", new BigDecimal("12.9783000"), new BigDecimal("77.6408000"), "Metro Pillar 82"));
                BusStop s9 = stopRepository.save(new BusStop("Domlur Flyover Stop", new BigDecimal("12.9609000"), new BigDecimal("77.6416000"), "Beside EGL Tech Park"));
                BusStop s10 = stopRepository.save(new BusStop("Koramangala Water Tank", new BigDecimal("12.9345000"), new BigDecimal("77.6254000"), "Sony World Junction"));
                BusStop s11 = stopRepository.save(new BusStop("Engineering Block 3", new BigDecimal("12.8630000"), new BigDecimal("77.6655000"), "Academic Plaza Bay"));

                routeStopRepository.save(new RouteStop(r2, s8, 1, "07:45 AM"));
                routeStopRepository.save(new RouteStop(r2, s9, 2, "07:58 AM"));
                routeStopRepository.save(new RouteStop(r2, s10, 3, "08:12 AM"));
                routeStopRepository.save(new RouteStop(r2, s5, 4, "08:25 AM"));
                routeStopRepository.save(new RouteStop(r2, s11, 5, "08:40 AM"));

                // Bus assignments
                assignmentRepository.save(new BusAssignment(b1, d1, r1, LocalDate.now(), "ACTIVE"));
                assignmentRepository.save(new BusAssignment(b2, d2, r2, LocalDate.now(), "ACTIVE"));

                // Bus locations
                locationRepository.save(new BusLocation(b1, new BigDecimal("12.9351000"), new BigDecimal("77.6012000"), new BigDecimal("38.50"), "ON_ROUTE", true));
                locationRepository.save(new BusLocation(b2, new BigDecimal("12.9609000"), new BigDecimal("77.6416000"), new BigDecimal("42.00"), "ON_ROUTE", true));
                locationRepository.save(new BusLocation(b3, new BigDecimal("12.9767500"), new BigDecimal("77.5712550"), BigDecimal.ZERO, "NOT_STARTED", true));

                // Trips
                tripRepository.save(new Trip(b1, d1, r1, LocalDateTime.now().minusMinutes(35), "ON_ROUTE", s4));
                tripRepository.save(new Trip(b2, d2, r2, LocalDateTime.now().minusMinutes(20), "ON_ROUTE", s9));

                // Demo Students
                User uStudent1 = userRepository.save(new User(
                        "REG2024001", "aaron@transitx.edu", passwordEncoder.encode("student123"),
                        Role.ROLE_STUDENT, "Aaron Sharma", "9123456780"
                ));
                Student stu1 = new Student(uStudent1, "REG2024001", "Computer Science & Engineering", "3rd Year", "Aaron Sharma", "aaron@transitx.edu", "9123456780");
                stu1.setAssignedBus(b1);
                stu1.setAssignedRoute(r1);
                Student savedStu1 = studentRepository.save(stu1);

                User uStudent2 = userRepository.save(new User(
                        "REG2024002", "priya@transitx.edu", passwordEncoder.encode("student123"),
                        Role.ROLE_STUDENT, "Priya Nair", "9123456781"
                ));
                Student stu2 = new Student(uStudent2, "REG2024002", "Information Technology", "2nd Year", "Priya Nair", "priya@transitx.edu", "9123456781");
                stu2.setAssignedBus(b2);
                stu2.setAssignedRoute(r2);
                studentRepository.save(stu2);

                // Sample bus pass
                String passCode = "TXPASS-2024-REG2024001";
                String qrData = "TRANSITX|PASS:" + passCode + "|STUDENT:REG2024001|ROUTE:RT-101|BUS:BUS-01|BOARDING:Dairy Circle|VALID_TILL:" + LocalDate.now().plusMonths(6);
                passRepository.save(new StudentBusPass(
                        passCode, savedStu1, r1, b1, s4, PassStatus.APPROVED,
                        LocalDate.now(), LocalDate.now().plusMonths(6),
                        "/uploads/id_proof_reg2024001.jpg", qrData
                ));
            }
        };
    }
}
