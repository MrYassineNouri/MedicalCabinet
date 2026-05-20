package models;

public class Consultation {
    private int id;
    private int patientId;
    private String consultationDate;
    private String notes;
    private String diagnosis;
    private String prescription;

    public Consultation(int patientId, String consultationDate, String notes, String diagnosis, String prescription) {
        this.patientId = patientId;
        this.consultationDate = consultationDate;
        this.notes = notes;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
    }

    public Consultation(int id, int patientId, String consultationDate, String notes, String diagnosis, String prescription) {
        this.id = id;
        this.patientId = patientId;
        this.consultationDate = consultationDate;
        this.notes = notes;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public String getConsultationDate() { return consultationDate; }
    public String getNotes() { return notes; }
    public String getDiagnosis() { return diagnosis; }
    public String getPrescription() { return prescription; }

    public void setId(int id) { this.id = id; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public void setConsultationDate(String consultationDate) { this.consultationDate = consultationDate; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }
    public void setPrescription(String prescription) { this.prescription = prescription; }
}