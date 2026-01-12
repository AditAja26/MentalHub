# Digital Mental Health Literacy Hub

## 1. Project Overview

This project implements a **Digital Mental Health Literacy Hub** based on the provided use‑case diagram. It is a **Java Spring MVC (Servlet-based) web application** that supports students, mental health advisors, and admins.

The goal of this README is to:

* Explain how to **run and configure** the system
* Clearly show **what is implemented vs not implemented**
* Map **each UC (Use Case)** to code modules
* Help the team **organize remaining work** and update implementation status over time

---

## 2. Technology Stack

* Java (Spring MVC – Servlet based)
* Maven
* JSP / HTML / CSS
* Apache Tomcat
* Relational Database (MySQL / PostgreSQL)

---

## 3. Project Structure

```
MentalHub/
├── pom.xml
├── src/main/java/com/
│   ├── controller/        # MVC Controllers
│   ├── model/             # Domain Models
│   └── services/          # Business Logic
├── src/main/webapp/
│   ├── WEB-INF/
│   │   ├── views/          # UI modules (by feature)
│   │   ├── dispatcher-servlet.xml
│   │   └── web.xml
│   └── resources/          # CSS, images
```

---

## 4. Requirements (Before Deployment)

### 4.1 Database (Required)

This project **will NOT run correctly without a database**.

You must have:

* MySQL or PostgreSQL running
* A created database (name it `mentalhub`)
* Valid DB user + password

Expected tables (minimum):

* users
* roles
* appointments
* forum_posts
* educational_content

(Adjust based on implementation)

---

## 5. How to Deploy

1. Configure database connection (datasource)
2. Build project using Maven
3. Deploy WAR to Tomcat
4. Start server

---

## 6. After Deployment (What To Do Next)

### Application Entry Point

* **Home Page:** `http://localhost:8080/mentalhub/student`

### Initial Setup Steps

1. Register at least one user
2. Manually assign roles if needed (DB or Admin UI)
3. Log in using student/admin/advisor roles
4. Verify access to assigned modules

---

## 7. Use Case Implementation Status

### Authentication Module

| UC ID | Use Case | Description             | Status      |
| ----- | -------- | ----------------------- | ----------- |
| UC006 | Login    | User authentication     | Implemented |
| UC007 | Logout   | End user session        | Implemented |
| UC008 | Register | Create new user account | Implemented |

**Controller:** `AuthController`

---

### Profile Module

| UC ID | Use Case                    | Description         | Status      |
| ----- | --------------------------- | ------------------- | ----------- |
| UC009 | Manage Personal Information | View & edit profile | Implemented |

**Controller:** `ProfileController`

---

### Student Support Module

| UC ID | Use Case                  | Description             | Status                                 |
| ----- | ------------------------- | ----------------------- | -------------------------------------- |
| UC003 | Book Appointment          | Book counseling session | Implemented                            |
| UC004 | Attend Virtual Counseling | Join counseling session | UI Implemented / Logic Not Implemented |

**Controller:** `StudentController`

---

### Monitoring & Analysis Module

| UC ID | Use Case          | Description           | Status          |
| ----- | ----------------- | --------------------- | --------------- |
| UC002 | Monitor Dashboard | View student progress | Not Implemented |
| UC010 | Generate Report   | Advisor reports       | Not Implemented |

**Controller:** `AdvisorController`

---

### Notification Module

| UC ID | Use Case             | Description        | Status                                   |
| ----- | -------------------- | ------------------ | ---------------------------------------- |
| UC005 | Receive Notification | View notifications | UI Implemented / Backend Not Implemented |

**Controller:** (Partial / View-based)

---

### Mental Health Literacy Module

| UC ID | Use Case                  | Description     | Status          |
| ----- | ------------------------- | --------------- | --------------- |
| UC013 | View Educational Content  | Read articles   | Implemented     |
| UC014 | Take Knowledge Check Quiz | Quiz assessment | Not Implemented |

**Controller:** `MentalHealthLiteracyController`

---

### Peer Support Module

| UC ID | Use Case              | Description            | Status      |
| ----- | --------------------- | ---------------------- | ----------- |
| UC015 | Create Forum Post     | Create peer discussion | Implemented |
| UC016 | View Peer Discussions | View & comment posts   | Implemented |

**Controller:** `PeerSupportController`

---

### Admin Module

| UC ID | Use Case                   | Description         | Status      |
| ----- | -------------------------- | ------------------- | ----------- |
| UC011 | Manage Users               | View & manage users | Implemented |
| UC012 | Manage Educational Content | Add/edit articles   | Implemented |

**Controller:** `AdminController`

---

## 8. How to Mark a UC as Implemented

When you complete a feature:

1. Implement backend logic (Controller + Service)
2. Ensure UI is functional
3. Update this README:

   * Change status to **Implemented**
   * Add notes if needed

This keeps the whole team aligned.

**If your code was marked as 'Implemented' and it isn't, update it to say "Not Implemented"**

---

## 9. Recommended Next Tasks

* Implement real persistence (JPA + Database)
* Complete Notification backend
* Implement Quiz logic (UC014)
* Secure role-based access control
* Add validation & error handling

---

## 10. Ownership & Workflow

* One UC = one owner
* Update README after each merge
* Use UC IDs in commit messages

Example:

```
feat(UC014): implement knowledge check quiz
```

---

**This README is a living document. Keep it updated.**
