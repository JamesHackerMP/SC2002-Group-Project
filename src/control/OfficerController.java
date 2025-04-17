package control;

import control.interfaces.officer.*;
import entity.HDBOfficer;
import entity.Project;
import java.util.List;

public class OfficerController implements OfficerRegistrationController, OfficerApprovalController {
    private final ProjectController projectController;
    private final ApplicationController applicationController;

    public OfficerController(ProjectController projectController, ApplicationController applicationController) {
        this.projectController = projectController;
        this.applicationController = applicationController;
    }

    @Override
    public boolean registerForProject(HDBOfficer officer, String projectName) {
        Project newProject = projectController.getProject(projectName);
        if (newProject == null) return false;

        List<Project> currentProjects = projectController.getProjectsByOfficer(officer.getName());
        for (Project project : currentProjects) {
            if (isOverlappingPeriod(project, newProject)) {
                return false;
            }
        }

        if (newProject.getOfficers().contains(officer.getName()) ||
                newProject.getPendingOfficers().contains(officer.getName())) {
            return false;
        }

        if (newProject.getOfficers().size() >= newProject.getOfficerSlots()) {
            return false;
        }

        newProject.addPendingOfficer(officer.getName());
        return true;
    }
    
    @Override
    public boolean approveOfficerRegistration(String officerName, String projectName) {
        Project project = projectController.getProject(projectName);
        if (project == null || !project.getPendingOfficers().contains(officerName)) {
            return false;
        }
        if (applicationController.getAllApplications().stream()
            .anyMatch(application -> 
                application.getApplicantName().equals(officerName) &&
                application.getProjectName().equals(projectName))) {
                return false;}
        project.addOfficer(officerName);
        projectController.saveProjects();
        project.removePendingOfficer(officerName);
        return true;
    }

    @Override
    public boolean rejectOfficerRegistration(String officerName, String projectName) {
        Project project = projectController.getProject(projectName);
        return project != null && project.removePendingOfficer(officerName);
    }

    private boolean isOverlappingPeriod(Project p1, Project p2) {
        return !p1.getClosingDate().isBefore(p2.getOpeningDate()) &&
                !p2.getClosingDate().isBefore(p1.getOpeningDate());
    }
}