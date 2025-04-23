package control.interfaces.application;

import entity.Application;
import entity.Application.Status;
import java.util.List;

public interface ApplicationManagementController {
    boolean applyForProject(String applicantName, String projectName, String flatTypeApply);
    Application getApplication(String applicantName);
    boolean requestWithdrawal(String applicantName);
    boolean approveApplication(String applicantName);
    boolean rejectApplication(String applicantName);
    boolean bookFlat(String applicantName, String flatType, String officerName);
    String checkApplicantName(String applicantName);
    String checkProjectName(String applicantName);
    String checkFlatTypeApply(String applicantName);
    Status checkStatus(String applicantName);
    String checkFlatType(String applicantName);
    int checkPrice(String applicantName);
    List<Application> getAllApplications();
    boolean isStatusPending(String applicantName);
    boolean isStatusSuccessful(String applicantName);
    boolean isStatusUnsuccessful(String applicantName);
    boolean isStatusBooked(String applicantName);
}