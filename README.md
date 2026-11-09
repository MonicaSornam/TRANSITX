# TRANSITX – Smart Campus Transportation and Bus Tracking Management System

> **Tagline:** *"Your Campus. Your Bus. Your Journey."*

TRANSITX is a complete, full-stack, enterprise-grade college transportation management and real-time tracking web application. It unifies college transit operations across three primary modules—**Students**, **Drivers**, and **Campus Administrators**—providing live bus tracking, sequential route schedules, verified driver credentials, and end-to-end digital bus pass management with cryptographic QR code verification.

---

## 1. Technology Stack

- **Backend:** Java 17+, Spring Boot 3.2.x / 3.3.x, Spring Web, Spring Data JPA, Spring Security (JWT + BCrypt)
- **Database:** MySQL (`campus_bus_tracking`)
- **Build Tool:** Apache Maven
- **Frontend:** Semantic HTML5, Modern CSS3, Vanilla JavaScript (ES6+)
- **Mapping & GIS:** Leaflet.js & OpenStreetMap
- **Architecture:** Clean Layered Architecture (`controller`, `service`, `repository`, `entity`, `dto`, `config`, `security`, `exception`)

---

## 2. Brand Identity & Official Color Palette

| Role | Color Name | Hex Code | Visual Application |
|---|---|---|---|
| **Primary Header & Brand** | **Navy** | `#2F4156` | Main navigation bar, dashboard sidebar, footer, primary headings, logo text |
| **Action & Accent** | **Teal** | `#567C8D` | Primary buttons, active nav items, icons, highlights, links, action buttons |
| **Highlight & Cards** | **Sky Blue** | `#C8D9E6` | Dashboard highlights, info cards, route info sections, statistics backgrounds |
| **Canvas & Body** | **Beige** | `#F5EFEB` | Main website background, login background, registration background, section backgrounds |
| **Surfaces & Modals** | **White** | `#FFFFFF` | Content cards, forms, tables, dashboard containers, modal backgrounds |

---

## 3. Project Directory Structure

```
TRANSITX/
├── backend/
│   └── src/
│       ├── main/
│       │   ├── java/com/transitx/
│       │   │   ├── config/          (SecurityConfig, WebConfig)
│       │   │   ├── controller/      (Auth, Bus, Route, Driver, Student, Tracking, BusPass, Trip, Admin)
│       │   │   ├── dto/             (ApiResponse, AuthDTO, BusDTO, RouteDTO, StopDTO, LocationDTO, PassDTO, etc.)
│       │   │   ├── entity/          (User, Bus, Route, BusStop, RouteStop, Driver, Student, BusPass, Trip, etc.)
│       │   │   ├── exception/       (GlobalExceptionHandler, ResourceNotFoundException, BadRequestException)
│       │   │   ├── repository/      (11 Spring Data JPA Repositories)
│       │   │   ├── security/        (JwtTokenProvider, JwtAuthenticationFilter, UserDetailsServiceImpl)
│       │   │   ├── service/         (Auth, Bus, Route, Driver, Student, Tracking, BusPass, Trip, Admin)
│       │   │   └── TransitXApplication.java
│       │   └── resources/
│       │       └── application.properties
├── frontend/
│   ├── css/
│   │   ├── style.css               (Global design system & color palette variables)
│   │   ├── dashboard.css           (Sidebar layouts, tables, stat cards, digital bus pass)
│   │   └── responsive.css          (Mobile and tablet breakpoints, print styles)
│   ├── js/
│   │   ├── api.js                  (Fetch wrapper, JWT authorization headers, toast alerts)
│   │   ├── auth.js                 (Login, register, role guards, logout, navbar state)
│   │   ├── student.js              (Student dashboard, assigned transit info)
│   │   ├── routes.js               (Route search filter, sequential stops modal)
│   │   ├── tracking.js             (Leaflet.js map, OpenStreetMap, simulated GPS animation, ETA)
│   │   ├── bus-pass.js             (Apply form, QR code generator, print/PDF pass)
│   │   ├── driver.js               (Driver trip start/end, status broadcaster, location transmitter)
│   │   └── admin.js                (Metrics, buses CRUD, routes CRUD, driver allocations, pass reviews)
│   ├── index.html                  (Landing page with Hero, Services, Routes, How It Works)
│   ├── about.html                  (About TRANSITX, campus fleet metrics, architectural pillars)
│   ├── register.html               (Student account registration with field validation)
│   ├── login.html                  (Student portal login)
│   ├── student-dashboard.html      (Student control center with assigned transit & pass status)
│   ├── bus-routes.html             (Interactive route browser with live search & stop sequence)
│   ├── bus-tracking.html           (Leaflet.js live bus telemetry with simulated GPS demo mode)
│   ├── driver-details.html         (Verified driver directory with experience and vehicle allocation)
│   ├── bus-pass.html               (Bus pass application form with route & boarding point selection)
│   ├── my-bus-pass.html            (Official Digital Bus Pass with QR code verification & print)
│   ├── driver-login.html           (Dedicated driver authentication portal)
│   ├── driver-dashboard.html       (Driver trip dispatch, status switcher, GPS location updater)
│   ├── admin-login.html            (Administrative console login)
│   ├── admin-dashboard.html        (Comprehensive transport manager control center)
│   └── contact.html                (Campus transport control room, helpline, inquiry form)
├── pom.xml                         (Maven build configuration with all dependencies)
├── database.sql                    (Complete MySQL schema & demonstration seed data)
└── README.md                       (Full documentation and execution manual)
```

---

## 4. Database Schema (`campus_bus_tracking`)

The system uses 11 relational tables configured with foreign keys, uniqueness constraints, and timestamps:

1. `users` – Base user authentication table (id, username, email, password, role, full_name, phone, enabled).
2. `buses` – College vehicle fleet (id, bus_number, capacity, status, model_plate).
3. `routes` – Transit corridors (id, route_name, route_code, start_point, end_point, departure_time, arrival_time, est_duration, distance_km).
4. `bus_stops` – Geographic stop coordinates (id, stop_name, latitude, longitude, landmark).
5. `route_stops` – Sequential stop ordering (id, route_id, stop_id, stop_sequence, pickup_time).
6. `drivers` – Staff credentials & license (id, user_id, driver_code, full_name, phone, license_no, experience_years, assigned_bus_id).
7. `students` – Enrolled riders (id, user_id, reg_no, department, year_of_study, full_name, email, phone, assigned_bus_id, assigned_route_id).
8. `bus_assignments` – Operational vehicle allocations (id, bus_id, driver_id, route_id, assigned_date, status).
9. `student_bus_pass` – Pass issuance & QR data (id, pass_code, student_id, route_id, bus_id, boarding_stop_id, status, validity_start, validity_end, qr_code_data).
10. `bus_locations` – Real-time telemetry (id, bus_id, latitude, longitude, speed, status, last_updated, is_simulated).
11. `trips` – Shift logs (id, bus_id, driver_id, route_id, start_time, end_time, trip_status, current_stop_id).

---

## 5. Demonstration & Default Credentials

| Role | Username / ID | Password | Portal URL |
|---|---|---|---|
| **Administrator** | `admin` | `admin123` | `http://localhost:8080/admin-login.html` |
| **Driver 1** | `DRV001` | `driver123` | `http://localhost:8080/driver-login.html` |
| **Driver 2** | `DRV002` | `driver123` | `http://localhost:8080/driver-login.html` |
| **Student 1** | `REG2024001` | `student123` | `http://localhost:8080/login.html` |
| **Student 2** | `REG2024002` | `student123` | `http://localhost:8080/login.html` |

---

## 6. Setup & Execution Instructions

### Prerequisites
1. **Java 17 or higher** (`java -version`)
2. **Apache Maven 3.8+** (included in `C:\tools\apache-maven-3.9.16\bin` or system PATH)
3. **Oracle MySQL Server 8.x** running on port `3306`

### Step 1: Initialize Database
Open MySQL Workbench or MySQL CLI:
```bash
mysql -u root -p < C:\TRANSITX\database.sql
```
*(Alternatively, copy and paste the contents of `database.sql` into MySQL Workbench and run.)*

### Step 2: Configure Database Credentials
In `backend/src/main/resources/application.properties`, confirm your MySQL password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/campus_bus_tracking?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 3: Compile & Run the Spring Boot Backend
In VS Code terminal or PowerShell:
```powershell
cd C:\TRANSITX
mvn spring-boot:run
```
*(Or use `& "C:\tools\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run`)*

The Spring Boot backend will start on **`http://localhost:8080`**.

### Step 4: Open Frontend
You can open `frontend/index.html` directly in any web browser, or serve it through VS Code Live Server, or visit `http://localhost:8080/index.html` (since Spring Boot's WebConfig is configured to serve the `frontend/` directory).

---

## 7. Key Features Demonstrated in Viva / Review

1. **Live GPS Bus Tracking with Leaflet.js**:
   - Open `bus-tracking.html`.
   - Click **Start Simulation** to observe the bus marker moving in real-time along sequential route nodes.
   - Speed calculation, ETA countdown, and approaching stop dynamically update.
   - Prominently displays the required `DEMO MODE` badge.

2. **Digital Bus Pass Generation & QR Verification**:
   - Log in as student (`REG2024001` / `student123`).
   - Open **My Bus Pass** (`my-bus-pass.html`).
   - Observe the Navy and Sky Blue pass card, pass details, and live generated QR Code.
   - Click **Print Bus Pass** to preview printable layout.
   - Public QR verification endpoint available at `/api/bus-pass/verify/{passCode}`.

3. **Driver Trip Console**:
   - Log in as driver (`DRV001` / `driver123`).
   - Click **Start Trip** to activate the bus and broadcast status to students.
   - Report traffic delays or destination arrival.

4. **Administrative Fleet Control Panel**:
   - Log in as admin (`admin` / `admin123`).
   - View 6 real-time metrics.
   - Add/edit/delete buses and routes.
   - Review pending student bus pass applications, click **Approve**, and specify validity duration.
