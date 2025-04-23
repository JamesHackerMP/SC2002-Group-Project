package boundary.interfaces.applicant;

public interface EnquiryApplicantUI {
    void displayEnquiryMenu(String userName);
    void createEnquiry(String userName);
    void viewMyEnquiries(String userName);
    void displayEnquiry(int index, String enquiryId, boolean canReply);
    void editEnquiry(String userName);
    void deleteEnquiry(String userName);
}