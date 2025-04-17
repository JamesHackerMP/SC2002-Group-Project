package control.interfaces.project;

import entity.Project;

public interface ProjectManagementController {
    boolean createProject(Project project);
    boolean updateProject(Project project);
    boolean deleteProject(String projectName);
    boolean saveProjects();
    void validateManagerProjects(String managerName);
}