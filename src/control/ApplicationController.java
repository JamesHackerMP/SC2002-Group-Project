package control;

import control.interfaces.application.*;
import entity.*;
import java.util.*;

public class ApplicationController implements ApplicationEligibilityController, 
                                            ApplicationManagementController,
                                            ApplicationQueryController {
    private final Map<String, Application> applications;
    private final ProjectController projectController;

    public ApplicationController(ProjectController projectController) {
        this.applications = new HashMap<>();
        this.projectController = projectController;
    }

    @Override
    public boolean isEligibleForProject(User applicant, Project project) {
        if (applications.values().stream()
            .anyMatch(application -> 
                application.getApplicantName().equals(applicant.getName()) &&
                application.getProjectName().equals(project.getName()))) {
                return true;}
        if (applicant.getMaritalStatus().equalsIgnoreCase("Single")) {
            if (applicant.getAge() < 35) {
                return false;
            }
            if (project.getTwoRoomUnits() == 0) {
                return false;
            }
        } else if (applicant.getMaritalStatus().equalsIgnoreCase("Married")) {
            if (applicant.getAge() < 21) {
                return false;
            }
        } else {
            return false;
        }
        if ((!project.isOpenForApplication())) {
            return false;
        }
        if (applicant instanceof HDBOfficer) {
            if (project == projectController.getCurrentProjectByOfficer(applicant.getName())) {
                return false;
            }
            List<Project> pendingProjects = projectController.getAllProjects().stream()
                .filter(pendingProject -> pendingProject.getPendingOfficers().contains(applicant.getName()))
                .toList();
            if (pendingProjects.contains(project)) {
            return false;
            }
        }
        else {if (!project.isVisible()){return false; } } 
        return true;
    }

    @Override
    public boolean applyForProject(User applicant, String projectName, String flatTypeApply) {
        if (applications.values().stream()
            .anyMatch(app -> app.getApplicantName().equals(applicant.getName()) && 
                 app.getStatus() != Application.Status.UNSUCCESSFUL)) {
            return false;
        }
       
        if (!isEligibleForProject(applicant, projectController.getProject(projectName))) {
            return false;
        }

        Application application = new Application(applicant.getName(), projectName, flatTypeApply);
        applications.put(applicant.getName(), application);
        return true;
    }

    @Override
    public Application getApplication(String applicantName) {
        return applications.get(applicantName);
    }

    @Override
    public boolean requestWithdrawal(String applicantName) {
        Application application = applications.get(applicantName);
        if (application == null || application.getStatus() != Application.Status.PENDING || application.getStatus() != Application.Status.SUCCESSFUL) {
            return false;
        }
        application.setStatus(Application.Status.UNSUCCESSFUL);
        return true;
    }
    
    @Override
    public boolean approveApplication(String applicantName) {
        Application application = applications.get(applicantName);
        if (application == null || application.getStatus() != Application.Status.PENDING) {
            return false;
        }
        application.setStatus(Application.Status.SUCCESSFUL);
        return true;
    }

    @Override
    public boolean rejectApplication(String applicantName) {
        Application application = applications.get(applicantName);
        if (application == null || application.getStatus() != Application.Status.PENDING) {
            return false;
        }
        application.setStatus(Application.Status.UNSUCCESSFUL);
        return true;
    }

    @Override
    public boolean bookFlat(String applicantName, String flatType, String officerName) {
        Application application = applications.get(applicantName);
        if (application == null || application.getStatus() != Application.Status.SUCCESSFUL) {
            return false;
        }

        Project project = projectController.getProject(application.getProjectName());
        if (project == null) {
            return false;
        }
        
        if (project.bookFlat(flatType, applicantName, officerName)) {
            application.setStatus(Application.Status.BOOKED);
            application.setFlatType(flatType);
            if (flatType.equals("2-Room")) {application.setPrice(project.getTwoRoomPrice()); }
            else {application.setPrice(project.getThreeRoomPrice()); }
            return true;
        }
        else {return false;}
    }

    @Override
    public List<Application> getAllApplications() {
        return new ArrayList<>(applications.values());
    }
}