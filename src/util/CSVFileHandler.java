package util;

import entity.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVFileHandler implements FileDataHandler {
    private static final String APPLICANT_PATH = "src\\data\\ApplicantList.csv";
    private static final String OFFICER_PATH = "src\\data\\OfficerList.csv";
    private static final String MANAGER_PATH = "src\\data\\ManagerList.csv";
    private static final String PROJECT_PATH = "src\\data\\ProjectList.csv";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    
    @Override
    public List<String[]> readCSV(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                data.add(values);
            }
        }
        return data;
    }
    
    @Override
    public void writeCSV(String filePath, List<String[]> data, String[] headers) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            if (headers != null) {
                bw.write(String.join(",", headers));
                bw.newLine();
            }

            for (String[] row : data) {
                bw.write(String.join(",", row));
                bw.newLine();
            }
        }
    }
    
    @Override
    public void saveUsers(Map<String, User> users) throws IOException {
        List<String[]> applicantData = new ArrayList<>();
        List<String[]> officerData = new ArrayList<>();
        List<String[]> managerData = new ArrayList<>();

        String[] userHeaders = {"Name", "NRIC", "Age", "Marital Status", "Password"};

        for (User user : users.values()) {
            String[] record = {
                    user.getName(),
                    user.getNric(),
                    String.valueOf(user.getAge()),
                    user.getMaritalStatus(),
                    user.getPassword()
            };

            if (user instanceof Applicant) {
                applicantData.add(record);
            } else if (user instanceof HDBOfficer) {
                officerData.add(record);
            } else if (user instanceof HDBManager) {
                managerData.add(record);
            }
        }

        writeCSV(APPLICANT_PATH, applicantData, userHeaders);
        writeCSV(OFFICER_PATH, officerData, userHeaders);
        writeCSV(MANAGER_PATH, managerData, userHeaders);
    }
    
    @Override
    public void saveProjects(List<Project> projects) throws IOException {
        List<String[]> projectData = new ArrayList<>();
        String[] headers = {
                "Project Name", "Neighborhood", "Type 1", "Number of units for Type 1",
                "Selling price for Type 1", "Type 2", "Number of units for Type 2",
                "Selling price for Type 2", "Application opening date",
                "Application closing date", "Manager", "Officer Slot", "Officer"
        };

        for (Project project : projects) {
            String[] record = new String[13];
            record[0] = project.getName();
            record[1] = project.getNeighborhood();
            record[2] = "2-Room";
            record[3] = project.getThreeRoomUnits() > 0 ? String.valueOf(project.getTwoRoomUnits()) : "";
            record[4] = project.getThreeRoomUnits() > 0 ? String.valueOf(project.getTwoRoomPrice()) : "";
            record[5] = project.getThreeRoomUnits() > 0 ? "3-Room" : "";
            record[6] = project.getThreeRoomUnits() > 0 ? String.valueOf(project.getThreeRoomUnits()) : "";
            record[7] = project.getThreeRoomUnits() > 0 ? String.valueOf(project.getThreeRoomPrice()) : "";
            record[8] = project.getOpeningDate().format(DATE_FORMATTER);
            record[9] = project.getClosingDate().format(DATE_FORMATTER);
            record[10] = project.getManager();
            record[11] = String.valueOf(project.getOfficerSlots());
            record[12] = String.join(";", project.getOfficers());

            projectData.add(record);
        }

        writeCSV(PROJECT_PATH, projectData, headers);
    }
    
    @Override
    public List<User> loadApplicants() throws IOException {
        return loadUsers(APPLICANT_PATH, UserType.APPLICANT);
    }
    
    @Override
    public List<User> loadOfficers() throws IOException {
        return loadUsers(OFFICER_PATH, UserType.OFFICER);
    }
    
    @Override
    public List<User> loadManagers() throws IOException {
        return loadUsers(MANAGER_PATH, UserType.MANAGER);
    }
    
    private List<User> loadUsers(String filePath, UserType type) throws IOException {
        List<User> users = new ArrayList<>();
        List<String[]> userData = readCSV(filePath);
        
        for (String[] record : userData) {
            String name = record[0];
            String nric = record[1];
            int age = Integer.parseInt(record[2]);
            String maritalStatus = record[3];
            String password = record[4];
            
            User user = null;
            switch (type) {
                case APPLICANT -> user = new Applicant(name, nric, age, maritalStatus, password);
                case OFFICER -> user = new HDBOfficer(name, nric, age, maritalStatus, password);
                case MANAGER -> user = new HDBManager(name, nric, age, maritalStatus, password);
            }
            
            if (user != null) {
                users.add(user);
            }
        }
        
        return users;
    }
    
    @Override
    public List<Project> loadProjects() throws IOException {
        List<Project> projects = new ArrayList<>();
        List<String[]> projectData = readCSV(PROJECT_PATH);
        
        for (String[] record : projectData) {
            String name = record[0];
            String neighborhood = record[1];
            int twoRoomUnits = record[3].isEmpty() ? 0 : Integer.parseInt(record[3]);
            int twoRoomPrice = record[4].isEmpty() ? 0 : Integer.parseInt(record[4]);
            int threeRoomUnits = record[6].isEmpty() ? 0 : Integer.parseInt(record[6]);
            int threeRoomPrice = record[7].isEmpty() ? 0 : Integer.parseInt(record[7]);
            LocalDate openDate = LocalDate.parse(record[8], DATE_FORMATTER);
            LocalDate closeDate = LocalDate.parse(record[9], DATE_FORMATTER);
            String manager = record[10];
            int officerSlots = Integer.parseInt(record[11]);
            
            Project project = new Project(name, neighborhood, twoRoomUnits, twoRoomPrice,
                    threeRoomUnits, threeRoomPrice, openDate, closeDate, manager, officerSlots);
            
            if (record.length > 12 && record[12] != null && !record[12].isEmpty()) {
                String[] officerList = record[12].split(";");
                for (String officer : officerList) {
                    if (!officer.isEmpty()) {
                        project.addOfficer(officer);
                    }
                }
            }
            
            projects.add(project);
        }
        
        return projects;
    }
    
    private enum UserType {
        APPLICANT, OFFICER, MANAGER
    }
}