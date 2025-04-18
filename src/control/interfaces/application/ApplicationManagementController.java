package control.interfaces.application;

import entity.Application;
import entity.User;

public interface ApplicationManagementController {
    boolean applyForProject(User applicant, String projectName, String flatTypeApply);
    Application getApplication(String applicantName);
    boolean requestWithdrawal(String applicantName);
    boolean approveApplication(String applicantName);
    boolean rejectApplication(String applicantName);
    boolean bookFlat(String applicantName, String flatType, String officerName);
}