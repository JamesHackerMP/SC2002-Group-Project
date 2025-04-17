package entity.interfaces.enquiry;

import java.time.LocalDateTime;

public interface EnquiryIdentification {
    String getId();
    String getApplicantName();
    String getProjectName();
    LocalDateTime getCreatedDate();
    String getFormattedCreatedDate();
}