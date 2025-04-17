package boundary.interfaces.manager;

import entity.HDBManager;

public interface ProjectManagementManagerUI {
    void createProject(HDBManager manager);
    void editProject(HDBManager manager);
    void deleteProject(HDBManager manager);
    void toggleVisibility(HDBManager manager);
    void viewAllProjects();
}