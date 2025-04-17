package control.interfaces.enquiry;

import entity.Enquiry;

public interface EnquiryCreationController {
    Enquiry createEnquiry(String applicantName, String projectName, String question);
    boolean updateEnquiry(String enquiryId, String newQuestion);
    boolean deleteEnquiry(String enquiryId);
}