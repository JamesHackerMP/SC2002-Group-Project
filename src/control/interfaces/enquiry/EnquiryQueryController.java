package control.interfaces.enquiry;

import entity.Enquiry;
import java.time.LocalDateTime;
import java.util.List;

public interface EnquiryQueryController {
    Enquiry getEnquiry(String enquiryId);
    List<String> getEnquiriesByApplicant(String applicantName);
    List<String> getEnquiriesByProject(String projectName);
    List<String> getAllEnquiries();
        String checkApplicantName(String enquiryId);
    String checkProjectName(String enquiryId);
    String checkQuestion(String enquiryId);
    String checkAnswer(String enquiryId);
    LocalDateTime checkCreatedDate(String enquiryId);
    LocalDateTime checkAnsweredDate(String enquiryId);
    String checkFormattedCreatedDate(String enquiryId);
    String checkFormattedAnsweredDate(String enquiryId);
}