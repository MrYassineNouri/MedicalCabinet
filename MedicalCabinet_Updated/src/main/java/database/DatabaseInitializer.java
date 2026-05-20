package database;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void initialize() {
        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) {
                System.err.println("Cannot initialize database - connection failed");
                return;
            }

            Statement stmt = conn.createStatement();


            // Create patients table
            String patientTable = """
                CREATE TABLE patients (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    firstName TEXT NOT NULL,
                    lastName TEXT NOT NULL,
                    email TEXT,
                    phone TEXT NOT NULL,
                    age INTEGER NOT NULL
                )
            """;

            // Create appointments table
            String appointmentTable = """
                CREATE TABLE appointments (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patientId INTEGER NOT NULL,
                    patientName TEXT NOT NULL,
                    appointmentDate TEXT NOT NULL,
                    appointmentTime TEXT NOT NULL,
                    reason TEXT,
                    FOREIGN KEY(patientId) REFERENCES patients(id) ON DELETE CASCADE
                )
            """;

            // Create consultations table
            String consultationTable = """
                CREATE TABLE consultations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    patientId INTEGER NOT NULL,
                    consultationDate TEXT NOT NULL,
                    notes TEXT,
                    diagnosis TEXT,
                    prescription TEXT,
                    FOREIGN KEY(patientId) REFERENCES patients(id) ON DELETE CASCADE
                )
            """;

            stmt.execute(patientTable);
            stmt.execute(appointmentTable);
            stmt.execute(consultationTable);

            System.out.println("Database tables created successfully!");
            stmt.close();

        } catch (Exception e) {
            System.err.println("Database Initialization Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}