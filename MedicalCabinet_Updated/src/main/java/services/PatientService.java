package services;

import database.DatabaseConnection;
import models.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PatientService {

    public void addPatient(Patient patient) {

        String sql =
                "INSERT INTO patients(firstName,lastName,phone,age) VALUES(?,?,?,?)";

        try (Connection conn = DatabaseConnection.connect()) {

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setString(3, patient.getPhone());
            pstmt.setInt(4, patient.getAge());

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updatePatient(Patient patient) {

        String sql = "UPDATE patients SET firstName=?, lastName=?, phone=?, age=? WHERE id=?";

        try (Connection conn = DatabaseConnection.connect()) {

            PreparedStatement pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, patient.getFirstName());
            pstmt.setString(2, patient.getLastName());
            pstmt.setString(3, patient.getPhone());
            pstmt.setInt(4, patient.getAge());
            pstmt.setInt(5, patient.getId());

            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deletePatient(int id) {

        String sql = "DELETE FROM patients WHERE id=?";

        try (Connection conn = DatabaseConnection.connect()) {

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Patient> searchPatients(String keyword) {

        List<Patient> patients = new ArrayList<>();

        String sql = """
                SELECT * FROM patients
                WHERE lower(firstName) LIKE ?
                OR lower(lastName) LIKE ?
                OR lower(phone) LIKE ?
                """;

        try (Connection conn = DatabaseConnection.connect()) {

            PreparedStatement pstmt = conn.prepareStatement(sql);

            String search = "%" + keyword.toLowerCase() + "%";

            pstmt.setString(1, search);
            pstmt.setString(2, search);
            pstmt.setString(3, search);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("phone"),
                        rs.getInt("age")
                );

                patients.add(patient);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return patients;
    }

    public List<Patient> getAllPatients() {

        List<Patient> patients = new ArrayList<>();

        String sql = "SELECT * FROM patients";

        try (Connection conn = DatabaseConnection.connect()) {

            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {

                Patient patient = new Patient(
                        rs.getInt("id"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("phone"),
                        rs.getInt("age")
                );

                patients.add(patient);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return patients;
    }
}
