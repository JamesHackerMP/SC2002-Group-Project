package boundary.interfaces.applicant;

import entity.User;

public interface EnquiryApplicantUI {
    void displayEnquiryMenu(User user);
    void createEnquiry(User user);
    void viewMyEnquiries(User user);
    void editEnquiry(User user);
    void deleteEnquiry(User user);
}