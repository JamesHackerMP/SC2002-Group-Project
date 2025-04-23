package boundary.interfaces.user;

public interface NotificationUI {
    void displayNotifications(String userName);
    
    void displayApplicantNotifications(String applicant);
    
    void displayOfficerNotifications(String officer);
    
    void displayManagerNotifications(String manager);
}