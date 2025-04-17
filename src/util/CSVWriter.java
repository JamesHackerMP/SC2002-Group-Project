package util;
import entity.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CSVWriter {
    public static void writeCSV(String filePath, List<String[]> data, String[] headers) throws IOException {
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

    public static void saveUsers(Map<String, User> users) throws IOException {
        List<String[]> applicantData = new ArrayList<>();
        List<String[]> officerData = new ArrayList<>();
        List<String[]> managerData = new ArrayList<>();

        String[] applicantHeaders = {"Name","NRIC","Age","Marital Status","Password"};
        String[] officerHeaders = {"Name","NRIC","Age","Marital Status","Password"};
        String[] managerHeaders = {"Name","NRIC","Age","Marital Status","Password"};

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

        writeCSV("src\\data\\ApplicantList.csv", applicantData, applicantHeaders);
        writeCSV("src\\data\\OfficerList.csv", officerData, officerHeaders);
        writeCSV("src\\data\\ManagerList.csv", managerData, managerHeaders);
    }

    public static void saveProjects(List<Project> projects) throws IOException {
        List<String[]> projectData = new ArrayList<>();
        String[] headers = {
                "Project Name", "Neighborhood", "Type 1", "Number of units for Type 1",
                "Selling price for Type 1", "Type 2", "Number of units for Type 2",
                "Selling price for Type 2", "Application opening date",
                "Application closing date", "Manager", "Officer Slot", "Officer"
        };

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        for (Project project : projects) {
            String[] record = new String[13];
            record[0] = project.getName();
            record[1] = project.getNeighborhood();
            record[2] = "2-Room";
            record[3] = String.valueOf(project.getTwoRoomUnits());
            record[4] = String.valueOf(project.getTwoRoomPrice());
            record[5] = project.getThreeRoomUnits() > 0 ? "3-Room" : "";
            record[6] = project.getThreeRoomUnits() > 0 ? String.valueOf(project.getThreeRoomUnits()) : "";
            record[7] = project.getThreeRoomUnits() > 0 ? String.valueOf(project.getThreeRoomPrice()) : "";
            record[8] = project.getOpeningDate().format(formatter);
            record[9] = project.getClosingDate().format(formatter);
            record[10] = project.getManager();
            record[11] = String.valueOf(project.getOfficerSlots());
            record[12] = String.join(";", project.getOfficers());

            projectData.add(record);
        }

        writeCSV("src\\data\\ProjectList.csv", projectData, headers);
    }
}