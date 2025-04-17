package boundary.interfaces.applicant;

import entity.User;

public interface ApplicationManagementUI {
    void applyForProject(User user);
    void viewMyApplication(User user);
    void withdrawApplication(User user);
}