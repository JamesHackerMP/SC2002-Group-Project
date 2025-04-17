package control;

import control.interfaces.manager.*;
import entity.Application;
import entity.HDBManager;
import entity.Project;
import entity.Report;
import entity.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ManagerController implements ProjectManagementManagerController, 
                                          OfficerManagementController,
                                          ReportGenerationController {
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final OfficerController officerController;
    private final AuthenticationController authController;

    public ManagerController(ProjectController projectController, ApplicationController applicationController, OfficerController officerController, AuthenticationController authController) {
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.officerController = officerController;
        this.authController = authController;
    }

    @Override
    public boolean createProject(HDBManager manager, Project project) {
        try {
            projectController.validateManagerProjects(manager.getName());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
            return false;
        }
    
        List<Project> currentProjects = projectController.getProjectsByManager(manager.getName());
        for (Project p : currentProjects) {
            if (isOverlappingPeriod(p, project)) {
                return false;
            }
        }
    
        project.setManager(manager.getName());
        return projectController.createProject(project);
    }

    public List<String> getPendingOfficers(Project project) {
        return project.getPendingOfficers();
    }

    @Override
    public boolean approveOfficer(String officerName, String projectName) {
        return officerController.approveOfficerRegistration(officerName, projectName);
    }

    @Override
    public boolean rejectOfficer(String officerName, String projectName) {
        return officerController.rejectOfficerRegistration(officerName, projectName);
    }

    private boolean isOverlappingPeriod(Project p1, Project p2) {
        return !p1.getClosingDate().isBefore(p2.getOpeningDate()) &&
                !p2.getClosingDate().isBefore(p1.getOpeningDate());
    }

    @Override
    public boolean toggleProjectVisibility(String projectName, boolean visible) {
        Project project = projectController.getProject(projectName);
        if (project == null) {
            return false;
        }
        project.setVisible(visible);
        return true;
    }

    @Override
    public Report generateApplicationsReport(Map<String, String> filters) {
        List<String[]> reportData = new ArrayList<>();
        List<Application> applications = applicationController.getAllApplications();

        for (Application app : applications) {
            boolean include = true;

            if (filters != null) {
                for (Map.Entry<String, String> entry : filters.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();

                    switch (key) {
                        case "status" -> {
                            if (!app.getStatus().toString().equalsIgnoreCase(value)) {
                                include = false;
                            }
                        }
                        case "maritalStatus" -> {
                            User applicant = authController.getUser(app.getApplicantName());
                            if (applicant == null || !applicant.getMaritalStatus().equalsIgnoreCase(value)) {
                                include = false;
                            }
                        }
                        case "flatType" -> {
                            if (app.getFlatType() == null || !app.getFlatType().equalsIgnoreCase(value)) {
                                include = false;
                            }
                        }
                        case "projectName" -> {
                            if (!app.getProjectName().equalsIgnoreCase(value)) {
                                include = false;
                            }
                        }
                        case "minAge" -> {
                            User applicant = authController.getUser(app.getApplicantName());
                            if (applicant == null || applicant.getAge() < Integer.parseInt(value)) {
                                include = false;
                            }
                        }
                        case "maxAge" -> {
                            User applicant = authController.getUser(app.getApplicantName());
                            if (applicant == null || applicant.getAge() > Integer.parseInt(value)) {
                                include = false;
                            }
                        }
                    }

                    if (!include) break;
                }
            }

            if (include) {
                User applicant = authController.getUser(app.getApplicantName());
                String[] row = {
                    app.getApplicantName(),
                    app.getProjectName(),
                    app.getStatus().toString(),
                    app.getFlatType() != null ? app.getFlatType() : "N/A",
                    applicant != null ? String.valueOf(applicant.getAge()) : "N/A",
                    applicant != null ? applicant.getMaritalStatus() : "N/A"
                };
                reportData.add(row);
            }
        }

        return new Report("REP" + System.currentTimeMillis(), "Applications", filters, reportData);
    }
}