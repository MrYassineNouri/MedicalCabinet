package services;

import database.DatabaseConnection;
import models.Consultation;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultationService {

    public void addConsultation(Consultation consultation) {
        String sql = "INSERT INTO consultations(patientId, consultationDate, notes, diagnosis, prescription) VALUES(?,?,?,?,?)";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, consultation.getPatientId());
            pstmt.setString(2, consultation.getConsultationDate());
            pstmt.setString(3, consultation.getNotes());
            pstmt.setString(4, consultation.getDiagnosis());
            pstmt.setString(5, consultation.getPrescription());

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Consultation> getConsultationsByPatientId(int patientId) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT * FROM consultations WHERE patientId = ? ORDER BY consultationDate DESC";

        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Consultation consultation = new Consultation(
                        rs.getInt("id"),
                        rs.getInt("patientId"),
                        rs.getString("consultationDate"),
                        rs.getString("notes"),
                        rs.getString("diagnosis"),
                        rs.getString("prescription")
                );
                consultations.add(consultation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return consultations;
    }
}