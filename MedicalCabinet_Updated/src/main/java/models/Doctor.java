package models;

public class Doctor extends Person {

    private String speciality;

    public Doctor(String firstName, String lastName, String speciality) {
        super(firstName, lastName);
        this.speciality = speciality;
    }

    public String getSpeciality() {
        return speciality;
    }
}