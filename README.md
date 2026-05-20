# 🏥 Medical Cabinet Management System

A desktop application built in Java for **private practice doctors** to manage their patients, appointments, and medical consultations — all in one place, with PDF export and email delivery built in.

---

## Overview

Medical Cabinet is a JavaFX-based desktop application that gives independent doctors a clean, professional interface to handle their daily patient management tasks without relying on expensive third-party software.

---

## Features

- **Patient Management** — Add, update, delete, and search patients with full contact details
- **Appointment Scheduling** — Book appointments with date, time, and reason; conflict detection included
- **Consultation Records** — Log diagnoses, prescriptions, and clinical notes per patient
- **Medical PDF Export** — Generate a full patient report (appointments + consultations) as a PDF file
- **Email Delivery** — Send the medical report directly to the patient's email as an attachment
- **Live Statistics** — Dashboard header shows total patients, appointments, and consultations in real time
- **Persistent Storage** — All data stored locally in an SQLite database

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI Framework | JavaFX |
| Database | SQLite via JDBC |
| PDF Generation | Apache PDFBox |
| Email Delivery | Jakarta Mail (Gmail SMTP) |
| Build Tool | Maven |
| Language | Java 17 |

---

## Project Structure

```
src/
├── app/
│   └── Main.java                  # Entry point
├── ui/
│   └── Dashboard.java             # Main JavaFX interface
├── models/
│   ├── Patient.java
│   ├── Appointment.java
│   └── Consultation.java
├── services/
│   ├── PatientService.java
│   ├── AppointmentService.java
│   ├── ConsultationService.java
│   └── EmailService.java
├── database/
│   ├── DatabaseConnection.java
│   └── DatabaseInitializer.java
└── exceptions/
    └── AppointmentConflictException.java
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven
- A Gmail account with 2-Step Verification enabled

### Installation

1. Clone the repository:
```bash
git clone https://github.com/MrYassineNouri/MedicalCabinet.git
cd MedicalCabinet
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn javafx:run
```

### Email Setup

To enable email delivery, open `EmailService.java` and set your credentials:

```java
private static final String SMTP_USER = "your_gmail@gmail.com";
private static final String SMTP_PASS = "your_gmail_app_password"; // 16-char App Password
private static final String FROM_EMAIL = "your_gmail@gmail.com";
```

To generate a Gmail App Password: Google Account → Security → App Passwords → Mail → Generate.

---

## How It Works

1. Add a patient with their name, phone, age, and email
2. Open their profile via the **View** button
3. Log appointments and consultations in the dedicated tabs
4. Click **PDF** to export a full report, or **Email** to send it directly to the patient

---

## OOP Concepts Used

- **Encapsulation** — All model fields are private with controlled getters/setters
- **Inheritance** — `Dashboard extends Application` (JavaFX abstract class)
- **Polymorphism** — `@Override` of `start()` and `getPasswordAuthentication()`
- **Generics** — `ObservableList<Patient>`, `List<Appointment>`, `TableColumn<Patient, String>`
- **Custom Exceptions** — `AppointmentConflictException` for scheduling conflict handling
- **Collections** — `List`, `ObservableList` used throughout the service and UI layers

---

## Innovation Points

- SQLite database with JDBC — persistent local storage with foreign key relationships and cascade deletes
- PDF generation with Apache PDFBox — dynamic multi-section reports built programmatically
- Email with attachment via Jakarta Mail — MIME multipart messages over Gmail SMTP with TLS

---

## Author

**Yassine Nouri**  
Java Desktop Application — Academic Project
