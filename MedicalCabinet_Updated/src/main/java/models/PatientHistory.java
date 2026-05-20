package models;

public class PatientHistory {

    private int patientId;
    private String type;
    private String description;
    private String date;

    public PatientHistory(int patientId, String type, String description, String date) {
        this.patientId = patientId;
        this.type = type;
        this.description = description;
        this.date = date;
    }

    public int getPatientId() { return patientId; }
    public String getType() { return type; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
}