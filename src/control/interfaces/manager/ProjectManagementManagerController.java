package control.interfaces.manager;

public interface ProjectManagementManagerController {
    boolean approveOfficer(String officerName, String projectName);
    boolean rejectOfficer(String officerName, String projectName);
}