package control.interfaces.application;

public interface ApplicationEligibilityController {
    boolean isEligibleForProject(String applicantName, String projectName);
}