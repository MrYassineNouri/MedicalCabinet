package services;

import database.DatabaseConnection;
import models.Appointment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {

    public boolean addAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments(patientId, patientName, appointmentDate, appointmentTime, reason) VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) {
                System.err.println("Failed to connect to database");
                return false;
            }

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, appointment.getPatientId());
            pstmt.setString(2, appointment.getPatientName());
            pstmt.setString(3, appointment.getAppointmentDate());
            pstmt.setString(4, appointment.getAppointmentTime());
            pstmt.setString(5, appointment.getReason());

            int result = pstmt.executeUpdate();
            pstmt.close();

            if (result > 0) {
                System.out.println("Appointment added successfully!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE patientId = ? ORDER BY appointmentDate DESC, appointmentTime DESC";

        try (Connection conn = DatabaseConnection.connect()) {
            if (conn == null) return appointments;

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Appointment appointment = new Appointment(
                        rs.getInt("patientId"),
                        rs.getString("patientName"),
                        rs.getString("appointmentDate"),
                        rs.getString("appointmentTime"),
                        rs.getString("reason")
                );
                appointment.setId(rs.getInt("id"));
                appointments.add(appointment);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error loading appointments: " + e.getMessage());
            e.printStackTrace();
        }

        return appointments;
    }
}