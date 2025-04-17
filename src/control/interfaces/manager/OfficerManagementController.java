package control.interfaces.manager;

import entity.HDBManager;
import entity.Project;

public interface OfficerManagementController {
    boolean createProject(HDBManager manager, Project project);
    boolean toggleProjectVisibility(String projectName, boolean visible);
}