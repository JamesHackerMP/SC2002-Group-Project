package control;

import control.interfaces.authentication.*;
import entity.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import util.CSVReader;
import util.CSVWriter;

public class AuthenticationController implements UserAuthenticationController, 
                                               UserManagementController,
                                               UserPersistenceController {
    private final Map<String, User> users;
    private static final String DEFAULT_PASSWORD = "password";
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Z]$");

    public AuthenticationController() {
        this.users = new HashMap<>();
        loadUsers();
    }

    @Override
    public boolean checkNric(String nric) {
        return isValidNric(nric);
    }

    @Override
    public User authenticate(String nric, String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }

        User user = users.get(nric.toUpperCase());
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    @Override
    public boolean changePassword(String nric, String currentPassword, String newPassword) {
        if (!isValidNric(nric)) {
            return false;
        }

        User user = users.get(nric.toUpperCase());
        if (user == null || !user.getPassword().equals(currentPassword)) {
            return false;
        }

        if (newPassword == null || newPassword.isEmpty() || newPassword.equals(currentPassword)) {
            return false;
        }

        user.setPassword(newPassword);
        saveUsers();
        return true;
    }

    @Override
    public boolean resetPassword(String nric) {
        if (!isValidNric(nric)) {
            return false;
        }

        User user = users.get(nric.toUpperCase());
        if (user == null) {
            return false;
        }

        user.setPassword(DEFAULT_PASSWORD);
        return true;
    }

    @Override
    public User getUser(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }
    
        for (User user : users.values()) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
    
        return null;
    }

    @Override
    public boolean isValidNric(String nric) {
        return nric != null && NRIC_PATTERN.matcher(nric).matches();
    }

    @Override
    public boolean userExists(String nric) {
        return isValidNric(nric) && users.containsKey(nric.toUpperCase());
    }

    @Override
    public void saveUsers() {
        try {
            CSVWriter.saveUsers(users);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    @Override
    public int getUserCountByRole(String role) {
        int count = 0;
        for (User user : users.values()) {
            if (user.getRole().equalsIgnoreCase(role)) {
                count++;
            }
        }
        return count;
    }

    private void loadUsers() {
        try {
            loadApplicants();
            loadOfficers();
            loadManagers();
        } catch (IOException e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
    }

    private void loadApplicants() throws IOException {
        List<String[]> data = CSVReader.readCSV("src\\data\\ApplicantList.csv");
        for (String[] record : data) {
            if (record.length >= 5) {
                String name = record[0];
                String nric = record[1];
                int age = Integer.parseInt(record[2]);
                String maritalStatus = record[3];
                String password = record.length > 4 ? record[4] : DEFAULT_PASSWORD;

                users.put(nric, new Applicant(name, nric, age, maritalStatus, password));
            }
        }
    }

    private void loadOfficers() throws IOException {
        List<String[]> data = CSVReader.readCSV("src\\data\\OfficerList.csv");
        for (String[] record : data) {
            if (record.length >= 5) {
                String name = record[0];
                String nric = record[1];
                int age = Integer.parseInt(record[2]);
                String maritalStatus = record[3];
                String password = record.length > 4 ? record[4] : DEFAULT_PASSWORD;

                users.put(nric, new HDBOfficer(name, nric, age, maritalStatus, password));
            }
        }
    }

    private void loadManagers() throws IOException {
        List<String[]> data = CSVReader.readCSV("src\\data\\ManagerList.csv");
        for (String[] record : data) {
            if (record.length >= 5) {
                String name = record[0];
                String nric = record[1];
                int age = Integer.parseInt(record[2]);
                String maritalStatus = record[3];
                String password = record.length > 4 ? record[4] : DEFAULT_PASSWORD;

                users.put(nric, new HDBManager(name, nric, age, maritalStatus, password));
            }
        }
    }
}