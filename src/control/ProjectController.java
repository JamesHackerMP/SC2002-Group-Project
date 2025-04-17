package control;

import control.interfaces.project.*;
import entity.Project;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import util.CSVReader;
import util.CSVWriter;

public class ProjectController implements ProjectQueryController, ProjectManagementController {
    private final Map<String, Project> projects;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/M/d");

    public ProjectController() {
        this.projects = new HashMap<>();
        loadProjects();
    }

    private void loadProjects() {
        try {
            List<String[]> data = CSVReader.readCSV("src\\data\\ProjectList.csv");
            for (String[] record : data) {
                if (record.length >= 12) {
                    String name = record[0];
                    String neighborhood = record[1];
                    int twoRoomUnits = Integer.parseInt(record[3]);
                    int twoRoomPrice = Integer.parseInt(record[4]);
                    int threeRoomUnits = record[5].isEmpty() ? 0 : Integer.parseInt(record[6]);
                    int threeRoomPrice = record[5].isEmpty() ? 0 : Integer.parseInt(record[7]);
                    LocalDate openDate = LocalDate.parse(record[8], DATE_FORMATTER);
                    LocalDate closeDate = LocalDate.parse(record[9], DATE_FORMATTER);
                    String manager = record[10];
                    int officerSlots = Integer.parseInt(record[11]);
    
                    Project project = new Project(name, neighborhood, twoRoomUnits, twoRoomPrice,
                            threeRoomUnits, threeRoomPrice, openDate, closeDate, manager, officerSlots);
    
                    if (record.length > 12 && !record[12].isEmpty()) {
                        String[] officerList = record[12].split(";");
                        for (String officer : officerList) {
                            project.addOfficer(officer);
                        }
                    }
    
                    projects.put(name, project);
                }
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
        return new ArrayList<>(projects.values());
    }

    @Override
    public List<Project> getVisibleProjects() {
        List<Project> visibleProjects = new ArrayList<>();
        for (Project project : projects.values()) {
            if (project.isVisible()) {
                visibleProjects.add(project);
            }
        }
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
    public boolean saveProjects() {
        List<String[]> projectData = new ArrayList<>();
        String[] headers = {
                "Project Name", "Neighborhood", "Type 1", "Number of units for Type 1",
                "Selling price for Type 1", "Type 2", "Number of units for Type 2",
                "Selling price for Type 2", "Application opening date",
                "Application closing date", "Manager", "Officer Slot", "Officer"
        };

        for (Project project : projects.values()) {
            String[] record = new String[13];
            record[0] = project.getName();
            record[1] = project.getNeighborhood();
            record[2] = "2-Room";
            record[3] = String.valueOf(project.getTwoRoomUnits());
            record[4] = String.valueOf(project.getTwoRoomPrice());
            record[5] = project.getThreeRoomUnits() > 0 ? "3-Room" : "";
            record[6] = project.getThreeRoomUnits() > 0 ?
                    String.valueOf(project.getThreeRoomUnits()) : "";
            record[7] = project.getThreeRoomUnits() > 0 ?
                    String.valueOf(project.getThreeRoomPrice()) : "";
            record[8] = project.getOpeningDate().format(DATE_FORMATTER);
            record[9] = project.getClosingDate().format(DATE_FORMATTER);
            record[10] = project.getManager();
            record[11] = String.valueOf(project.getOfficerSlots());
            record[12] = String.join(";", project.getOfficers());

            projectData.add(record);
        }

        try {
            CSVWriter.writeCSV("src\\data\\ProjectList.csv", projectData, headers);
            return true;
        } catch (IOException e) {
            System.err.println("Error saving projects: " + e.getMessage());
            return false;
        }
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