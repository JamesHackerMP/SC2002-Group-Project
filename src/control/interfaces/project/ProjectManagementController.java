package control.interfaces.project;

import entity.Project;
import java.time.LocalDate;
import java.util.List;

public interface ProjectManagementController {
    boolean createProject(Project project);
    boolean updateProject(Project project);
    boolean deleteProject(String projectName);
    boolean saveProjects();
    void validateManagerProjects(String managerName);
     List<String> getVisibleProjectNames();
    List<String> getEligibleProjectNames(String userName);
    String checkNeighborhood(String projectName);
    int checkTwoRoomUnits(String projectName);
    int checkTwoRoomPrice(String projectName);
    int checkThreeRoomUnits(String projectName);
    int checkThreeRoomPrice(String projectName);
    LocalDate checkOpeningDate(String projectName);
    LocalDate checkClosingDate(String projectName);
    String checkManager(String projectName);
    List<String> checkOfficers(String projectName);
    int checkOfficerSlots(String projectName);
    boolean checkHasTwoRoomUnits(String projectName);
    boolean checkHasThreeRoomUnits(String projectName);
    boolean checkVisible(String projectName);
    List<String> checkPendingOfficers(String projectName);
}