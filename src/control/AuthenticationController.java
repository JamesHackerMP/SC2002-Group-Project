package control;

import control.interfaces.authentication.*;
import entity.*;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import util.FileDataHandler;

public class AuthenticationController implements UserAuthenticationController, 
                                               UserManagementController,
                                               UserPersistenceController {
    private final Map<String, User> users;
    private final FileDataHandler fileDataHandler;
    private static final String DEFAULT_PASSWORD = "password";
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Z]$");

    public AuthenticationController(FileDataHandler fileDataHandler) {
        this.fileDataHandler = fileDataHandler;
        this.users = new HashMap<>();
        loadUsers();
    }

    @Override
    public void saveUsers() {
        try {
            fileDataHandler.saveUsers(users);
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    private void loadUsers() {
        try {
            List<User> applicants = fileDataHandler.loadApplicants();
            List<User> officers = fileDataHandler.loadOfficers();
            List<User> managers = fileDataHandler.loadManagers();
            
            for (User user : applicants) {
                users.put(user.getNric().toUpperCase(), user);
            }
            
            for (User user : officers) {
                users.put(user.getNric().toUpperCase(), user);
            }
            
            for (User user : managers) {
                users.put(user.getNric().toUpperCase(), user);
            }
        } catch (IOException e) {
            System.err.println("Error loading user data: " + e.getMessage());
        }
    }

    @Override
    public boolean verifyNric(String nric) {
        return isValidNric(nric);
    }

    @Override
    public String authenticate(String nric, String password) {
        if (password == null || password.isEmpty()) {
            return null;
        }

        User user = users.get(nric.toUpperCase());
        if (user != null && user.getPassword().equals(password)) {
            return user.getName();
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
    public HDBOfficer getOfficer(String name) {
        User user = getUser(name);
        if (user != null && user instanceof HDBOfficer) {
            return (HDBOfficer) user;
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
    public int getUserCountByRole(String role) {
        int count = 0;
        for (User user : users.values()) {
            if (user.getRole().equalsIgnoreCase(role)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String checkRole(String userName) {
        return getUser(userName).getRole();
    }

    @Override
    public String checkMaritalStatus(String userName) {
        return getUser(userName).getMaritalStatus();
    }

    @Override
    public String checkNric(String userName) {
        return getUser(userName).getRole();
    }

    @Override
    public int checkAge(String userName) {
        return getUser(userName).getAge();
    }

}