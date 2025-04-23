package control;

import control.interfaces.application.*;
import entity.*;
import java.util.*;

public class ApplicationController implements ApplicationEligibilityController, 
                                            ApplicationManagementController,
                                            ApplicationQueryController {
    private final Map<String, Application> applications;
    private final ProjectController projectController;
    private final AuthenticationController authController;

    public ApplicationController(ProjectController projectController, AuthenticationController authController) {
        this.applications = new HashMap<>();
        this.projectController = projectController;
        this.authController = authController;
    }

    @Override
    public boolean isEligibleForProject(String applicantName, String projectName) {
        User applicant = authController.getUser(applicantName);
        Project project = projectController.getProject(projectName);
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
            if (project == projectController.getProject(projectController.getCurrentProjectByOfficer(applicant.getName()))) {
                return false;
            }
            List<String> projectNames = projectController.getAllProjects();
            List<Project> pendingProjects = new ArrayList<>();
            for (String name : projectNames) {
                Project pendingProject = projectController.getProject(name);
                if (pendingProject != null && project.getPendingOfficers().contains(applicant.getName())) {
                    pendingProjects.add(pendingProject);
                }
            }
            if (pendingProjects.contains(project)) {
            return false;
            }
        }
        else {if (!project.isVisible()){return false; } } 
        return true;
    }

    @Override
    public boolean applyForProject(String applicantName, String projectName, String flatTypeApply) {
        User applicant = authController.getUser(applicantName);
        if (applications.values().stream()
            .anyMatch(app -> app.getApplicantName().equals(applicant.getName()) && 
                 app.getStatus() != Application.Status.UNSUCCESSFUL)) {
            return false;
        }
       
        if (!isEligibleForProject(applicantName, projectName)) {
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
        if (application == null) {
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

    @Override
    public String checkApplicantName(String applicantName) { 
        return getApplication(applicantName).getApplicantName(); 
    }

    @Override
    public String checkProjectName(String applicantName) { 
        return getApplication(applicantName).getProjectName(); 
    }

    @Override
    public String checkFlatTypeApply(String applicantName) { 
        return getApplication(applicantName).getFlatTypeApply(); 
    }

    @Override
    public Application.Status checkStatus(String applicantName) { 
        return getApplication(applicantName).getStatus();
    }

    @Override
    public String checkFlatType(String applicantName) { 
        return getApplication(applicantName).getFlatType(); 
    }

    @Override
    public int checkPrice(String applicantName) { 
        return getApplication(applicantName).getPrice(); 
    }

    @Override
    public boolean isStatusPending(String applicantName) {
        return checkStatus(applicantName) == Application.Status.PENDING;
    }
    
    @Override
    public boolean isStatusSuccessful(String applicantName) {
        return checkStatus(applicantName) == Application.Status.SUCCESSFUL;
    }
    
    @Override
    public boolean isStatusUnsuccessful(String applicantName) {
        return checkStatus(applicantName) == Application.Status.UNSUCCESSFUL;
    }
    
    @Override
    public boolean isStatusBooked(String applicantName) {
        return checkStatus(applicantName) == Application.Status.BOOKED;
    }
}