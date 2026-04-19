# Todo Application (Session 4 & Session 5)

### Project Overview
This project is a **Spring Boot-based Todo Application**. It focuses on implementing a robust backend using **Java 17**, **Maven**, and a strict **Layered Architecture** while integrating a database via **Spring Data JPA**. 

The system provides a set of RESTful APIs to manage Todo tasks stored in an **H2 in-memory database**. Alongside core CRUD operations, it demonstrates advanced enterprise Spring concepts including Constructor Injection, Manual DTO-to-Entity mapping, Jakarta Validation, SLF4J Logging, External Service Simulation, and Global Exception Handling.

---

### Objectives
* **JPA & Database Integration:** Strict use of Spring Data JPA (`@Entity`, `@Table`, `@Id`) with an H2 in-memory database.
* **DTO Pattern:** Implementation of a strict DTO layer to ensure database entities are never directly exposed to the API client.
* **IoC & Dependency Injection:** Strict use of **Constructor Injection** (no `@Autowired` on fields).
* **Layered Architecture:** Clear separation between Controller, Service, Repository, and Client layers.
* **Validation & Exception Handling:** Using `@Valid` for request validation and providing meaningful JSON error responses via `@RestControllerAdvice`.
* **Logging & Traceability (Session 5):** Implementing **SLF4J** to track API requests, business logic flow, and application state.
* **Service Simulation (Session 5):** Integrating a dummy `NotificationServiceClient` to simulate interacting with external microservices/APIs.
* **High-Coverage Unit Testing (Session 5):** Implementing comprehensive test cases for the Service, Controller, Exception, and Mapper layers using **JUnit 5** and **Mockito**, achieving over **85% code coverage** verified by **JaCoCo**.

---

### Features

#### **1. Create TODO**
* **Endpoint:** `POST /todos`
* **Validation:** Uses `@Valid` to ensure the `title` is not null and is at least 3 characters long.
* **Logic:** Automatically sets the `createdAt` timestamp, defaults the `status` to `PENDING`, and triggers the simulated `NotificationServiceClient`.

#### **2. Retrieve TODOs**
* **Endpoint:** `GET /todos` (All) and `GET /todos/{id}` (By ID)
* **Logic:** Fetches data from the repository, maps it to a DTO, and safely returns it to the client. Returns custom `404 Not Found` responses if the ID does not exist.

#### **3. Update & Status Transitions**
* **Endpoint:** `PUT /todos/{id}`
* **Logic:** Allows updating of the title, description, and status. Implements strict business rules preventing invalid status transitions (e.g., blocks `COMPLETED` -> `PENDING`). 

#### **4. Delete TODO**
* **Endpoint:** `DELETE /todos/{id}`
* **Logic:** Completely removes the task from the database and returns a clear confirmation message.

#### **5. Simulated External Notifications (Session 5)**
* A mocked integration representing an external service (like SMS or Email). Upon successful creation of a Todo, the core service passes the data to the client which logs a success event.

#### **6. Comprehensive Logging (Session 5)**
* SLF4J logs are strategically placed across the Controller and Service layers to monitor incoming requests, database modifications, and simulated client calls.

---

### Architecture
The project follows a **Strict Layered Architecture**:

1.  **Controller:** Manages HTTP requests/responses, triggers validations, and logs incoming activity.
2.  **Service:** Orchestrates core business flow, handles status transition logic, coordinates mappings, and triggers external clients.
3.  **Repository:** Extends `JpaRepository` for abstracted database access.
4.  **Mapper:** Manages manual data conversion between `Todo` entities and `TodoDTO`s (`TodoMapper`).
5.  **Exception:** Centralized error handling logic (`GlobalExceptionHandler`, `ResourceNotFoundException`).
6.  **Client:** Manages external integrations and simulated outbound calls (`NotificationServiceClient`).
7.  **Model/DTO:** Defines the core `Todo` JPA Entity, `TodoStatus` Enum, and the client-facing `TodoDTO`.

---

### Tech Stack
* **Language:** Java 17
* **Framework:** Spring Boot 3.x
* **Database:** H2 In-Memory Database
* **ORM:** Spring Data JPA (Hibernate)
* **Build Tool:** Maven
* **Logging:** SLF4J
* **Testing & Coverage:** JUnit 5, Mockito, JaCoCo, Postman 

---

### API Endpoints

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| **POST** | `/todos` | Creates a new Todo task & triggers notification. |
| **GET** | `/todos` | Lists all Todo tasks in the system. |
| **GET** | `/todos/{id}` | Fetches a specific Todo task by its ID. |
| **PUT** | `/todos/{id}` | Updates an existing Todo task. |
| **DELETE**| `/todos/{id}` | Deletes a Todo task by its ID. |

---

### API Testing (Postman)

#### 1. CREATE TODO API (POST)
**Successful Creation (Auto-sets timestamp & default status)**
<img width="902" height="592" alt="image" src="https://github.com/user-attachments/assets/23cc300e-34ee-4662-81de-738261026b44" />

**Validation Failure (Title too short or null - Returns 400)**
<img width="902" height="497" alt="image" src="https://github.com/user-attachments/assets/c50e638c-0230-427f-8c22-dbf32d6044df" />

#### 2. GET TODOs API
**Get All Todos**
<img width="902" height="726" alt="image" src="https://github.com/user-attachments/assets/1f8f0a1a-49d3-4934-bcaf-a40af6c1b66a" />

**Get Todo by ID**
<img width="902" height="382" alt="image" src="https://github.com/user-attachments/assets/74730a9e-ef87-447a-9966-78991bf4aba6" />

#### 3. UPDATE TODO API (PUT)
**Successful Update (Status changed to COMPLETED)**
<img width="902" height="588" alt="image" src="https://github.com/user-attachments/assets/dd928de4-5754-4595-9dba-8a9e7b6278c0" />

#### 4. DELETE TODO API
**Successful Deletion (Returns 200 OK & Message)**
<img width="902" height="345" alt="image" src="https://github.com/user-attachments/assets/a69fa729-7eea-48a3-8201-1bf92da789fe" />

#### 5. EXCEPTION & EDGE CASE HANDLING
**Resource Not Found (Getting/Updating/Deleting an ID that doesn't exist)**
<img width="902" height="338" alt="image" src="https://github.com/user-attachments/assets/199dfdc4-2a1c-43ad-ad05-407b7cb6f94e" />

**Changing state from Completed to Pending not allowed**
<img width="902" height="597" alt="image" src="https://github.com/user-attachments/assets/ba19815d-ddd5-4a73-9bf4-2994c3345f16" />

---

## Session 5 Enhancements 

#### 1. SLF4J Logging & External Service Simulation
Console log proving that Controller/Service logging is active, and that the dummy `NotificationServiceClient` was successfully triggered upon TODO creation:
<img width="902" height="598" alt="Screenshot 2026-04-19 030349" src="https://github.com/user-attachments/assets/34daeef1-1e15-4fff-9f4d-2f9058536981" />

<img width="1772" height="268" alt="Screenshot 2026-04-19 202944" src="https://github.com/user-attachments/assets/f5c7683b-37db-45b2-9eeb-e4a7651f6f16" />

#### 2. High-Coverage Unit Testing
Running `mvn clean test` executing JUnit 5 and Mockito tests across all architectural layers:
<img width="1248" height="282" alt="Screenshot 2026-04-19 212233" src="https://github.com/user-attachments/assets/4a693643-7484-4460-aca9-07ab7403046d" />

#### 3. JaCoCo Test Coverage Report
Successfully exceeding the 85% required threshold across the application logic:
<img width="1325" height="403" alt="Screenshot 2026-04-19 212206" src="https://github.com/user-attachments/assets/5706625b-6429-4ec7-bd0a-3f1b7523ae93" />

---

### How to Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/swayam4621/Swayam_Joshi_NucleusTeq_Assignments.git
    ```
2.  **Navigate to the project:**
    ```bash
    cd Swayam_Joshi_java_training/session4/todo/
    ```
3.  **Build and Run:**
    ```bash
    mvn spring-boot:run
    ```
4.  **Access the API:** `http://localhost:8080/todos`
5.  **Access the H2 Database Console:** `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:tododb`, Username: `sa`, Password: `<blank>`)

---

### Author
**Swayam Joshi**
