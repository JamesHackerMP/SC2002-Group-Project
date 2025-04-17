package entity;

public class HDBOfficer extends User {

    public HDBOfficer(String name, String nric, int age, String maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
    }

    @Override
    public String getRole() {
        return "HDBOfficer";
    }
}