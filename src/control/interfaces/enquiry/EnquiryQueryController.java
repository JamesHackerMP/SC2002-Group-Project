package control.interfaces.enquiry;

import entity.Enquiry;
import java.util.List;

public interface EnquiryQueryController {
    Enquiry getEnquiry(String enquiryId);
    List<Enquiry> getEnquiriesByApplicant(String applicantName);
    List<Enquiry> getEnquiriesByProject(String projectName);
    List<Enquiry> getAllEnquiries();
}