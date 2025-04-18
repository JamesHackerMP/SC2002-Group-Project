package control;

import control.interfaces.project.*;
import entity.Project;
import java.io.IOException;
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
    public List<Project> getAllProjects() {
        List<Project> allProjects = new ArrayList<>(projects.values());
        allProjects.sort(Comparator.comparing(Project::getName, String.CASE_INSENSITIVE_ORDER));
        return allProjects;
    }
    
    @Override
    public List<Project> getVisibleProjects() {
        List<Project> visibleProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.isVisible()) {
                visibleProjects.add(project);
            }
        }
        visibleProjects.sort(Comparator.comparing(Project::getName, String.CASE_INSENSITIVE_ORDER));
        return visibleProjects;
    }

    @Override
    public boolean createProject(Project project) {
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
    public List<Project> getProjectsByManager(String managerName) {
        List<Project> managerProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.getManager().equalsIgnoreCase(managerName)) {
                managerProjects.add(project);
            }
        }
        return managerProjects;
    }

    @Override
    public Project getActiveProjectByManager(String managerName) {
        return projects.values().stream()
            .filter(project -> project.getManager().equalsIgnoreCase(managerName) && 
                    project.isOpenForApplication() &&
                    project.isVisible())
            .findFirst()
            .orElse(null);
    }

    @Override
    public List<Project> getProjectsByOfficer(String officerName) {
        List<Project> officerProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.getOfficers().contains(officerName)) {
                officerProjects.add(project);
            }
        }
        return officerProjects;
    }

    @Override
    public Project getCurrentProjectByOfficer(String officerName) {
        List<Project> officerProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.getOfficers().contains(officerName)) {
                officerProjects.add(project);
            }
        }
        for (Project project : officerProjects) {
            if (project.isOpenForApplication()) {
                return project;
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
}