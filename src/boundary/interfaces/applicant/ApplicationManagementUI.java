package boundary.interfaces.applicant;

public interface ApplicationManagementUI {
    void applyForProject(String userName);
    void viewMyApplication(String userName);
    void withdrawApplication(String userName);
}