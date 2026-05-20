package models;

public class Patient extends Person {

    private int id;
    private String phone;
    private int age;
    private String email;   // ✅ NEW

    public Patient(int id,
                   String firstName,
                   String lastName,
                   String phone,
                   int age,
                   String email) {

        super(firstName, lastName);
        this.id = id;
        this.phone = phone;
        this.age = age;
        this.email = email;
    }

    public Patient(String firstName,
                   String lastName,
                   String phone,
                   int age,
                   String email) {

        super(firstName, lastName);
        this.phone = phone;
        this.age = age;
        this.email = email;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public int getAge() { return age; }

    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }   // ✅ NEW

    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }

    public String getLastName() { return lastName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }
}