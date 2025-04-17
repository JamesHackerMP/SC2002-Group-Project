package control.interfaces.project;

import entity.Project;
import java.util.List;

public interface ProjectQueryController {
    Project getProject(String projectName);
    List<Project> getAllProjects();
    List<Project> getVisibleProjects();
    List<Project> getProjectsByManager(String managerName);
    List<Project> getProjectsByOfficer(String officerName);
    Project getCurrentProjectByOfficer(String officerName);
    boolean projectExists(String projectName);
    int getProjectCount();
}