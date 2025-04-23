package control;

import control.interfaces.project.*;
import entity.Project;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import util.FileDataHandler;

public class ProjectController implements ProjectQueryController, ProjectManagementController {
    private final Map<String, Project> projects;
    private final FileDataHandler fileDataHandler;

    public ProjectController(FileDataHandler fileDataHandler) {
        this.fileDataHandler = fileDataHandler;
        this.projects = new HashMap<>();
        loadProjects();
    }

    private void loadProjects() {
        try {
            List<Project> projectList = fileDataHandler.loadProjects();
            for (Project project : projectList) {
                projects.put(project.getName(), project);
            }
            
            Set<String> managers = new HashSet<>();
            for (Project project : projects.values()) {
                managers.add(project.getManager());
            }
    
            for (String manager : managers) {
                validateManagerProjects(manager);
            }
            
        } catch (IllegalStateException e) {
            System.err.println("Error during initialization: " + e.getMessage());
            throw e;
        } catch (IOException e) {
            System.err.println("Failed to load projects: " + e.getMessage());
        }
    }

    @Override
    public boolean saveProjects() {
        try {
            fileDataHandler.saveProjects(new ArrayList<>(projects.values()));
            return true;
        } catch (IOException e) {
            System.err.println("Error saving projects: " + e.getMessage());
            return false;
        }
    }

    @Override
    public void validateManagerProjects(String managerName) {
        List<Project> openProjects = projects.values().stream()
                .filter(project -> project.getManager().equalsIgnoreCase(managerName) && project.isOpenForApplication())
                .toList();
    
        if (openProjects.size() > 1) {
            throw new IllegalStateException("Manager " + managerName + " is managing more than one project that is open for application.");
        }
    }

    @Override
    public Project getProject(String projectName) {
        return projects.get(projectName);
    }

    @Override
    public List<String> getAllProjects() {
        List<String> allProjectNames = new ArrayList<>();
        List<Project> projectList = new ArrayList<>(projects.values());
        projectList.sort(Comparator.comparing(Project::getName, String.CASE_INSENSITIVE_ORDER));
        for (Project project : projectList) {
            allProjectNames.add(project.getName());
        }
        return allProjectNames;
    }
    
    @Override
    public List<String> getVisibleProjects() {
        List<String> visibleProjectNames = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.isVisible()) {
                visibleProjectNames.add(project.getName());
            }
        }
        visibleProjectNames.sort(String.CASE_INSENSITIVE_ORDER);
        return visibleProjectNames;
    }

    @Override
    public boolean createProject(String name, String neighborhood, int twoRoomUnits, int twoRoomPrice,
    int threeRoomUnits, int threeRoomPrice, LocalDate openingDate,
    LocalDate closingDate, String manager, int officerSlots) {
        Project project = new Project(name, neighborhood, twoRoomUnits, twoRoomPrice,
                threeRoomUnits, threeRoomPrice, openingDate,
                closingDate, manager, officerSlots);
        projects.put(project.getName(), project);
        return saveProjects();
    }

    @Override
    public boolean updateProject(Project project) {
        if (!projects.containsKey(project.getName())) {
            return false;
        }
        projects.put(project.getName(), project);
        return saveProjects();
    }

    @Override
    public boolean deleteProject(String projectName) {
        if (!projects.containsKey(projectName)) {
            return false;
        }
        projects.remove(projectName);
        return saveProjects();
    }

    @Override
    public List<String> getProjectsByManager(String managerName) {
        List<String> managerProjectNames = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.getManager().equalsIgnoreCase(managerName)) {
                managerProjectNames.add(project.getName());
            }
        }
        return managerProjectNames;
    }

    @Override
    public String getActiveProjectByManager(String managerName) {
        return projects.values().stream()
            .filter(project -> project.getManager().equalsIgnoreCase(managerName) && 
                    project.isOpenForApplication() &&
                    project.isVisible())
            .findFirst()
            .map(Project::getName)
            .orElse(null);
    }

    @Override
    public List<String> getProjectsByOfficer(String officerName) {
        List<String> officerProjectNames = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.getOfficers().contains(officerName)) {
                officerProjectNames.add(project.getName());
            }
        }
        return officerProjectNames;
    }

    @Override
    public String getCurrentProjectByOfficer(String officerName) {
        List<Project> officerProjectNames = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.getOfficers().contains(officerName)) {
                officerProjectNames.add(project);
            }
        }
        for (Project project : officerProjectNames) {
            if (project.isOpenForApplication()) {
                return project.getName();
            }
        }
        return null;
    }

    @Override
    public boolean projectExists(String projectName) {
        return projects.containsKey(projectName);
    }

    @Override
    public int getProjectCount() {
        return projects.size();
    }

    @Override
    public String checkNeighborhood(String projectName) {
        return getProject(projectName).getNeighborhood();
    }

    @Override
    public int checkTwoRoomUnits(String projectName) {
        return getProject(projectName).getTwoRoomUnits();
    }

    @Override
    public int checkTwoRoomPrice(String projectName) {
        return getProject(projectName).getTwoRoomPrice();
    }

    @Override
    public int checkThreeRoomUnits(String projectName) {
        return getProject(projectName).getThreeRoomUnits();
    }

    @Override
    public int checkThreeRoomPrice(String projectName) {
        return getProject(projectName).getThreeRoomPrice();
    }

    @Override
    public LocalDate checkOpeningDate(String projectName) {
        return getProject(projectName).getOpeningDate();
    }

    @Override
    public LocalDate checkClosingDate(String projectName) {
        return getProject(projectName).getClosingDate();
    }

    @Override
    public String checkManager(String projectName) {
        return getProject(projectName).getManager();
    }

    @Override
    public int checkOfficerSlots(String projectName) {
        return getProject(projectName).getOfficerSlots();
    }

    @Override
    public boolean checkVisible(String projectName) {
        return getProject(projectName).isVisible();
    }

    @Override
    public List<String> checkOfficers(String projectName) {
        return new ArrayList<>(getProject(projectName).getOfficers());
    }

    @Override
    public boolean checkHasTwoRoomUnits(String projectName) {
        return getProject(projectName).getTwoRoomUnits() > 0;
    }

    @Override
    public boolean checkHasThreeRoomUnits(String projectName) {
        return getProject(projectName).getThreeRoomUnits() > 0;
    }

    @Override
    public List<String> checkPendingOfficers(String projectName) {
        return getProject(projectName).getPendingOfficers();
    }

    @Override
    public void updateNeighborhood(String projectName, String neighborhood) {
        getProject(projectName).setNeighborhood(neighborhood);
        saveProjects();
    }


    @Override
    public void updateTwoRoomUnits(String projectName, int twoRoomUnits) {
        getProject(projectName).setTwoRoomUnits(twoRoomUnits);
        saveProjects();
    }

    @Override
    public void updateThreeRoomUnits(String projectName, int threeRoomUnits) {
        getProject(projectName).setThreeRoomUnits(threeRoomUnits);
        saveProjects();
    }

    @Override
    public void updateTwoRoomPrice(String projectName, int twoRoomPrice) {
        getProject(projectName).setTwoRoomPrice(twoRoomPrice);
        saveProjects();
    }

    @Override
    public void updateThreeRoomPrice(String projectName, int threeRoomPrice) {
        getProject(projectName).setThreeRoomPrice(threeRoomPrice);
        saveProjects();
    }

    @Override
    public void updateOpeningDate(String projectName, LocalDate openingDate) {
        getProject(projectName).setOpeningDate(openingDate);
        saveProjects();
    }

    @Override
    public void updateClosingDate(String projectName, LocalDate closingDate) {
        getProject(projectName).setClosingDate(closingDate);
        saveProjects();
    }

    @Override
    public void updateOfficerSlots(String projectName, int officerSlots) {
        getProject(projectName).setOfficerSlots(officerSlots);
        saveProjects();
    }

    @Override
    public List<String> getVisibleProjectNames() {
        return getVisibleProjects();
    }

    @Override
    public List<String> getEligibleProjectNames(String username) {
        List<String> eligibleProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.isVisible() && project.isOpenForApplication()) {
                eligibleProjects.add(project.getName());
            }
        }
        eligibleProjects.sort(String.CASE_INSENSITIVE_ORDER);
        return eligibleProjects;
    }
}