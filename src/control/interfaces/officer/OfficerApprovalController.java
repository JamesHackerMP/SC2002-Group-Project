package control.interfaces.officer;

public interface OfficerApprovalController {
    boolean approveOfficerRegistration(String officerName, String projectName);
    boolean rejectOfficerRegistration(String officerName, String projectName);
}