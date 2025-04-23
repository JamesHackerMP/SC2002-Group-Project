package entity;

import entity.interfaces.enquiry.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Enquiry implements EnquiryIdentification, EnquiryContent {
    public enum Status { PENDING, ANSWERED, CLOSED }

    private final String id;
    private final String applicantName;
    private final String projectName;
    private String question;
    private String answer;
    private final LocalDateTime createdDate;
    private LocalDateTime answeredDate;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd' | 'HH:mm:ss");

    public Enquiry(String id, String applicantName, String projectName, String question) {
        this.id = id;
        this.applicantName = applicantName;
        this.projectName = projectName;
        this.question = question;
        this.createdDate = LocalDateTime.now();
    }

    @Override
    public String getId() { return id; }
    
    @Override
    public String getApplicantName() { return applicantName; }
    
    @Override
    public String getProjectName() { return projectName; }
    
    @Override
    public String getQuestion() { return question; }
    
    @Override
    public String getAnswer() { return answer; }
    
    @Override
    public LocalDateTime getCreatedDate() { return createdDate; }
    
    @Override
    public LocalDateTime getAnsweredDate() { return answeredDate; }

    @Override
    public void setAnswer(String answer) {
        this.answer = answer;
        this.answeredDate = LocalDateTime.now();
    }
    
    @Override
    public void setQuestion(String question) {
        this.question = question;
    }

    @Override
    public String getFormattedCreatedDate() {
        return createdDate.format(DATE_TIME_FORMATTER);
    }

    @Override
    public String getFormattedAnsweredDate() {
        return answeredDate != null ? answeredDate.format(DATE_TIME_FORMATTER) : "Not answered yet";
    }
}