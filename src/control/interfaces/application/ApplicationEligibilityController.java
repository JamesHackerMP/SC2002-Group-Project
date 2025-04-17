package control.interfaces.application;

import entity.Project;
import entity.User;

public interface ApplicationEligibilityController {
    boolean isEligibleForProject(User applicant, Project project);
}