package control;

import control.interfaces.enquiry.*;
import entity.Enquiry;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnquiryController implements EnquiryCreationController,
                                         EnquiryQueryController,
                                         EnquiryResponseController {
    private final Map<String, Enquiry> enquiries;
    private int enquiryCounter;

    public EnquiryController() {
        this.enquiries = new HashMap<>();
        this.enquiryCounter = 1;
    }

    @Override
    public Enquiry createEnquiry(String applicantName, String projectName, String question) {
        String enquiryId = "ENQ" + enquiryCounter++;
        Enquiry enquiry = new Enquiry(enquiryId, applicantName, projectName, question);
        enquiries.put(enquiryId, enquiry);
        return enquiry;
    }

    @Override
    public boolean updateEnquiry(String enquiryId, String newQuestion) {
        Enquiry enquiry = enquiries.get(enquiryId);
        if (enquiry == null) {
            return false;
        }
        enquiry.setQuestion(newQuestion);
        return true;
    }

    @Override
    public boolean deleteEnquiry(String enquiryId) {
        if (!enquiries.containsKey(enquiryId)) {
            return false;
        }
        enquiries.remove(enquiryId);
        return true;
    }

    @Override
    public boolean replyToEnquiry(String enquiryId, String answer) {
        Enquiry enquiry = enquiries.get(enquiryId);
        if (enquiry == null) {
            return false;
        }
        enquiry.setAnswer(answer);
        return true;
    }

    @Override
    public Enquiry getEnquiry(String enquiryId) {
        return enquiries.get(enquiryId);
    }

    @Override
    public List<String> getEnquiriesByApplicant(String applicantName) {
        List<String> applicantEnquiryIds = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getApplicantName().equals(applicantName)) {
                applicantEnquiryIds.add(enquiry.getId());
            }
        }
        return applicantEnquiryIds;
    }
    
    @Override
    public List<String> getEnquiriesByProject(String projectName) {
        List<String> projectEnquiryIds = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getProjectName().equals(projectName)) {
                projectEnquiryIds.add(enquiry.getId());
            }
        }
        return projectEnquiryIds;
    }
    
    @Override
    public List<String> getAllEnquiries() {
        List<String> allEnquiryIds = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            allEnquiryIds.add(enquiry.getId());
        }
        return allEnquiryIds;
    }

    @Override
    public String checkApplicantName(String enquiryId) { return getEnquiry(enquiryId).getApplicantName(); }

    @Override
    public String checkProjectName(String enquiryId) { return getEnquiry(enquiryId).getProjectName(); }

    @Override
    public String checkQuestion(String enquiryId) { return getEnquiry(enquiryId).getQuestion(); }

    @Override
    public String checkAnswer(String enquiryId) { return getEnquiry(enquiryId).getAnswer(); }

    @Override
    public LocalDateTime checkCreatedDate(String enquiryId) { return getEnquiry(enquiryId).getCreatedDate(); }

    @Override
    public LocalDateTime checkAnsweredDate(String enquiryId) { return getEnquiry(enquiryId).getAnsweredDate(); }

    @Override
    public String checkFormattedCreatedDate(String enquiryId) {
        return getEnquiry(enquiryId).getFormattedCreatedDate();
    }

    @Override
    public String checkFormattedAnsweredDate(String enquiryId) {
        return getEnquiry(enquiryId).getFormattedAnsweredDate() != null ? getEnquiry(enquiryId).getFormattedAnsweredDate() : "Not answered yet";
    }
}