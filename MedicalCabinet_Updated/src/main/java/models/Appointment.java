package models;

public class Appointment {

    private int id;
    private int patientId;
    private String patientName;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;

    public Appointment(int patientId, String patientName, String appointmentDate, String appointmentTime, String reason) {
        this.patientId = patientId;
        this.patientName = patientName;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.reason = reason;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }
    public String getPatientName() { return patientName; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getReason() { return reason; }
}