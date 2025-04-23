package control;

import control.interfaces.manager.*;
import entity.Application;
import entity.Project;
import entity.Report;
import entity.User;

import java.time.LocalDateTime;
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
    private Report currentReport;

    public ManagerController(ProjectController projectController, ApplicationController applicationController, OfficerController officerController, AuthenticationController authController) {
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.officerController = officerController;
        this.authController = authController;
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

    @Override
    public void toggleProjectVisibility(String projectName, boolean visible) {
        Project project = projectController.getProject(projectName);
        project.setVisible(visible);
    }

    @Override
    public Report generateReport(Map<String, String> filters) {
        List<String[]> reportData = new ArrayList<>();
        List<String> applicantNames = applicationController.getAllApplications();
        List<Application> applications = new ArrayList<>();
        for (String applicantName : applicantNames) {
            Application app = applicationController.getApplication(applicantName);
            if (app != null) {
                applications.add(app);
            }
        }

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

        currentReport = new Report("REP" + System.currentTimeMillis(), "Applications", filters, reportData);
        return currentReport;
    }

    @Override
    public boolean generateApplicationsReport(Map<String, String> filters) {
        this.currentReport = generateReport(filters);
        return this.currentReport != null;
    }

    @Override
    public String getReportId() {
        return currentReport != null ? currentReport.getReportId() : "";
    }
    
    @Override
    public String getReportTitle() {
        return currentReport != null ? currentReport.getTitle() : "";
    }
    
    @Override
    public LocalDateTime getReportGeneratedDate() {
        return currentReport != null ? currentReport.getGeneratedDate() : null;
    }
    
    @Override
    public List<String[]> getReportData() {
        return currentReport != null ? currentReport.getData() : new ArrayList<>();
    }
    
    @Override
    public boolean hasReportData() {
        return currentReport != null && !currentReport.getData().isEmpty();
    }
}