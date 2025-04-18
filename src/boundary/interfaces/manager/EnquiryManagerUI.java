package boundary.interfaces.manager;

import entity.Enquiry;
import entity.HDBManager;

public interface EnquiryManagerUI {
    void manageEnquiries(HDBManager manager);
    void displayEnquiry(int index, Enquiry enquiry, boolean canReply);
}