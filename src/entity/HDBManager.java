package entity;

import java.util.ArrayList;
import java.util.List;

public class HDBManager extends User {
    private final List<String> managedProjects;

    public HDBManager(String name, String nric, int age, String maritalStatus, String password) {
        super(name, nric, age, maritalStatus, password);
        this.managedProjects = new ArrayList<>();
    }

    @Override
    public String getRole() {
        return "HDBManager";
    }

    public List<String> getManagedProjects() { return managedProjects; }
    public void addManagedProject(String projectName) { managedProjects.add(projectName); }
    public void removeManagedProject(String projectName) { managedProjects.remove(projectName); }
}