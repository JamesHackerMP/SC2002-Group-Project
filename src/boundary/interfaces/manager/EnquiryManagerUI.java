package boundary.interfaces.manager;

public interface EnquiryManagerUI {
    void manageEnquiries(String managerName);
    void displayEnquiry(int index, String enquiryId, boolean canReply);
}