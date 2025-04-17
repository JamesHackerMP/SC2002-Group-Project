package entity.interfaces.enquiry;

import entity.Enquiry;
import java.time.LocalDateTime;

public interface EnquiryContent {
    String getQuestion();
    void setQuestion(String question);
    String getAnswer();
    void setAnswer(String answer);
    LocalDateTime getAnsweredDate();
    String getFormattedAnsweredDate();
    Enquiry.Status getStatus();
    void setStatus(Enquiry.Status status);
    int getPriority();
    void setPriority(int priority);
    void closeEnquiry();
}