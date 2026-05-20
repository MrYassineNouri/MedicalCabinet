package services;

import database.DatabaseConnection;
import models.PatientHistory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryService {

    public void add(PatientHistory h) {

        String sql = "INSERT INTO history(patientId,type,description,date) VALUES(?,?,?,?)";

        try (Connection conn = DatabaseConnection.connect()) {

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, h.getPatientId());
            ps.setString(2, h.getType());
            ps.setString(3, h.getDescription());
            ps.setString(4, h.getDate());
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<PatientHistory> getByPatient(int id) {

        List<PatientHistory> list = new ArrayList<>();

        String sql = "SELECT * FROM history WHERE patientId=?";

        try (Connection conn = DatabaseConnection.connect()) {

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new PatientHistory(
                        rs.getInt("patientId"),
                        rs.getString("type"),
                        rs.getString("description"),
                        rs.getString("date")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}