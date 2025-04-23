package boundary.interfaces.manager;

public interface ProjectManagementManagerUI {
    void manageProjects(String managerName);
    void createProject(String managerName);
    void editProject(String managerName);
    void deleteProject(String managerName);
    void toggleVisibility(String managerName);
    void viewAllProjects();
}