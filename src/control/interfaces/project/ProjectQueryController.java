package control.interfaces.project;

import entity.Project;
import java.util.List;

public interface ProjectQueryController {
    Project getProject(String projectName);
    List<String> getAllProjects();
    List<String> getVisibleProjects();
    List<String> getProjectsByManager(String managerName);
    String getActiveProjectByManager(String managerName);
    List<String> getProjectsByOfficer(String officerName);
    String getCurrentProjectByOfficer(String officerName);
    boolean projectExists(String projectName);
    int getProjectCount();
}