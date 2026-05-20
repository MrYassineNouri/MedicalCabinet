package models;

public class Patient extends Person {

    private int id;
    private String phone;
    private int age;

    public Patient(int id,
                   String firstName,
                   String lastName,
                   String phone,
                   int age) {

        super(firstName, lastName);

        this.id = id;
        this.phone = phone;
        this.age = age;
    }

    public Patient(String firstName,
                   String lastName,
                   String phone,
                   int age) {

        super(firstName, lastName);

        this.phone = phone;
        this.age = age;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public int getAge() {
        return age;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
