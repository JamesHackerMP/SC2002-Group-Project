package entity.interfaces.enquiry;

import java.time.LocalDateTime;

public interface EnquiryContent {
    String getQuestion();
    void setQuestion(String question);
    String getAnswer();
    void setAnswer(String answer);
    LocalDateTime getAnsweredDate();
    String getFormattedAnsweredDate();
}