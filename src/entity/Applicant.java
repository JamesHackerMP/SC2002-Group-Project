package entity;

public class Applicant extends User {
    public Applicant(String name, String nric, int age, String maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
    }

    @Override
    public String getRole() {
        return "Applicant";
    }
}