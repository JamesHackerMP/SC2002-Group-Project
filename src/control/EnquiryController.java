package control;

import control.interfaces.enquiry.*;
import entity.Enquiry;
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
    public List<Enquiry> getEnquiriesByApplicant(String applicantName) {
        List<Enquiry> applicantEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getApplicantName().equals(applicantName)) {
                applicantEnquiries.add(enquiry);
            }
        }
        return applicantEnquiries;
    }

    @Override
    public List<Enquiry> getEnquiriesByProject(String projectName) {
        List<Enquiry> projectEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries.values()) {
            if (enquiry.getProjectName().equals(projectName)) {
                projectEnquiries.add(enquiry);
            }
        }
        return projectEnquiries;
    }

    @Override
    public List<Enquiry> getAllEnquiries() {
        return new ArrayList<>(enquiries.values());
    }
}