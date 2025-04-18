package boundary.interfaces.applicant;

import entity.Enquiry;
import entity.User;

public interface EnquiryApplicantUI {
    void displayEnquiryMenu(User user);
    void createEnquiry(User user);
    void viewMyEnquiries(User user);
    void displayEnquiry(int index, Enquiry enquiry, boolean canReply);
    void editEnquiry(User user);
    void deleteEnquiry(User user);
}