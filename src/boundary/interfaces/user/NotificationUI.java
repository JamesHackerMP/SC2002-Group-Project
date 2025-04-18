package boundary.interfaces.user;

import entity.User;

public interface NotificationUI {
    void displayNotifications(User user);
    
    void displayApplicantNotifications(User applicant);
    
    void displayOfficerNotifications(User officer);
    
    void displayManagerNotifications(User manager);
}