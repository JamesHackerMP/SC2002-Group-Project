package boundary;

import boundary.interfaces.user.*;
import control.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class UserUI implements AuthenticationUI, ProfileManagementUI, FilterManagementUI, MainMenuUI, NotificationUI {
    private final AuthenticationController authController;
    private final FilterController filterController;
    private final ApplicationController applicationController;
    private final ProjectController projectController;
    private final EnquiryController enquiryController;
    private final Scanner scanner;

    public UserUI(AuthenticationController authController, 
                FilterController filterController,
                ApplicationController applicationController,
                ProjectController projectController,
                EnquiryController enquiryController) {
        this.authController = authController;
        this.filterController = filterController;
        this.applicationController = applicationController;
        this.projectController = projectController;
        this.enquiryController = enquiryController;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public String login() {
        System.out.println("\n=== Login ===");
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine();

        if (authController.verifyNric(nric) == false) {
            System.out.println("Invalid NRIC. Please try again.");
            return null;
        }
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();        

        String userName = authController.authenticate(nric, password);
        if (userName == null) {
            System.out.println("Invalid password. Please try again.");
            return null;
        }

        filterController.setCurrentUser(userName);

        System.out.println("\nLogin successful! Welcome, " + userName + " (" + authController.checkRole(userName) + ")");
        
        displayNotifications(userName);
        
        return userName;
        }
        
        @Override
        public void displayNotifications(String userName) {
            switch (authController.checkRole(userName)) {
                case "Applicant" -> displayApplicantNotifications(userName);
                case "HDBOfficer" -> displayOfficerNotifications(userName);
                case "HDBManager" -> displayManagerNotifications(userName);
            }
        }
        
        @Override
        public void displayApplicantNotifications(String userName) {

            if (applicationController.getApplication(userName) != null) {
                System.out.println("\nNOTIFICATION: Your application for project " + 
                applicationController.checkProjectName(userName) + " is " + applicationController.checkStatus(userName));
            }
            
            List<String> answeredEnquiries = enquiryController.getEnquiriesByApplicant(userName).stream()
                    .filter(e -> enquiryController.getEnquiry(e) != null && !enquiryController.checkAnswer(e).isEmpty())
                    .toList();
            
            if (!answeredEnquiries.isEmpty()) {
                System.out.println("\nNOTIFICATION: You have " + answeredEnquiries.size() + 
                                " answered enquiries. Check the Manage Enquiry menu to view responses.");
            }
            
            if (applicationController.getApplication(userName) == null && answeredEnquiries.isEmpty()) {
                System.out.println("\nNOTIFICATION: No current applications or enquiry responses.");
            }
        }
        
        @Override
        public void displayOfficerNotifications(String userName) {
            String currentProjectName = projectController.getCurrentProjectByOfficer(userName);
            if (currentProjectName != null) {
                long pendingBookings = applicationController.getAllApplications().stream()
                .filter(a -> {
                    return applicationController.checkProjectName(a).equals(a) && 
                        applicationController.isStatusSuccessful(a);
                })
                .count();
                
                long unansweredEnquiries = enquiryController.getEnquiriesByProject(currentProjectName).stream()
                        .filter(e -> enquiryController.checkAnswer(e) == null)
                        .count();
                        
                if (pendingBookings > 0) {
                    System.out.println("\nNOTIFICATION: You have " + pendingBookings + 
                                    " approved applications waiting for booking.");
                }
                
                if (unansweredEnquiries > 0) {
                    System.out.println("\nNOTIFICATION: You have " + unansweredEnquiries + 
                                    " unanswered enquiries for project " + currentProjectName + ".");
                }
                
                if (pendingBookings == 0 && unansweredEnquiries == 0) {
                    System.out.println("\nNOTIFICATION: No pending applications or enquiries to process.");
                }
            }
        }
        
        @Override
        public void displayManagerNotifications(String userName) {
            List<String> managerProjectNames = projectController.getProjectsByManager(userName);
            if (!managerProjectNames.isEmpty()) {
                int totalPendingOfficers = 0;
                int totalPendingApplications = 0;
                int totalUnansweredEnquiries = 0;
                
                for (String projectName : managerProjectNames) {
                    totalPendingOfficers += projectController.checkPendingOfficers(projectName).size();
                    
                    long pendingApps = applicationController.getAllApplications().stream()
                            .filter(a -> {
                                return applicationController.checkProjectName(a).equals(projectName) && 
                                applicationController.isStatusPending(a);
                            })
                            .count();
                    totalPendingApplications += pendingApps;
                    
                    long unanswered = enquiryController.getEnquiriesByProject(projectName).stream()
                            .filter(e -> enquiryController.checkAnswer(e) == null)
                            .count();
                    totalUnansweredEnquiries += unanswered;
                }
                
                if (totalPendingOfficers > 0) {
                    System.out.println("\nNOTIFICATION: You have " + totalPendingOfficers + 
                                    " pending officer registrations.");
                }
                
                if (totalPendingApplications > 0) {
                    System.out.println("\nNOTIFICATION: You have " + totalPendingApplications + 
                                    " applications to process.");
                }
                
                if (totalUnansweredEnquiries > 0) {
                    System.out.println("\nNOTIFICATION: You have " + totalUnansweredEnquiries + 
                                    " unanswered enquiries across your projects.");
                }
                
                if (totalPendingOfficers == 0 && totalPendingApplications == 0 && totalUnansweredEnquiries == 0) {
                    System.out.println("\nNOTIFICATION: No pending tasks to handle.");
                }
            }
        }

    @Override
    public boolean changePassword(String userName) {
        System.out.println("\n=== Change Password ===");
        System.out.print("Enter Current Password: ");
        String currentPassword = scanner.nextLine();
        System.out.print("Enter New Password: ");
        String newPassword = scanner.nextLine();

        boolean success = authController.changePassword(authController.checkNric(userName), currentPassword, newPassword);
        if (success) {
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password. Please check your current password.");
        }
        return success;
    }

    @Override
    public void displayMainMenu(String userName) {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. View Profile");
        System.out.println("2. Change Password");
        System.out.println("3. Set Project Filters");
    
        switch (authController.checkRole(userName)) {
            case "Applicant" -> System.out.println("4. Applicant Menu");
            case "HDBOfficer" -> {
                System.out.println("4. HDB Officer Menu");
                System.out.println("5. Applicant Menu");
            }
            case "HDBManager" -> System.out.println("4. HDB Manager Menu");
        }
    
        System.out.println("0. Logout");
    }

    @Override
    public int getMenuChoice() {
        System.out.print("Enter your choice: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    public void displayProfile(String userName) {
        System.out.println("\n=== Your Profile ===");
        System.out.println("Name: " + userName);
        System.out.println("NRIC: " + authController.checkNric(userName));
        System.out.println("Age: " + authController.checkAge(userName));
        System.out.println("Marital Status: " + authController.checkMaritalStatus(userName));
        System.out.println("Role: " + authController.checkRole(userName));
    }

    @Override
    public void setFilters() {
        while (true) {
            System.out.println("\n=== Set Project Filters ===");
            System.out.println("Current filters:");
            System.out.println("Neighborhood: " + (filterController.checkNeighborhood() != null ? filterController.checkNeighborhood() : "Not set"));
            System.out.println("Flat Types: " + (filterController.checkFlatTypes() != null ? filterController.checkFlatTypes() : "Not set"));
            System.out.println("Opening After: " + (filterController.checkOpeningAfter() != null ? filterController.checkOpeningAfter() : "Not set"));
            System.out.println("Closing Before: " + (filterController.checkClosingBefore() != null ? filterController.checkClosingBefore() : "Not set"));
            System.out.println("Manager: " + (filterController.checkManager() != null ? filterController.checkManager() : "Not set"));
            System.out.println("Officer: " + (filterController.checkOfficer() != null ? filterController.checkOfficer() : "Not set"));
    
            System.out.println("\n0. Back");
            System.out.println("1. Set Neighborhood");
            System.out.println("2. Set Flat Types");
            System.out.println("3. Set Opening After");
            System.out.println("4. Set Closing Before");
            System.out.println("5. Set Manager");
            System.out.println("6. Set Officer");
            System.out.println("7. Clear All Filters");
    
            int choice = getMenuChoice();
            switch (choice) {
                case 0 -> {
                    return;
                }
                case 1 -> {
                    String neighborhood = getStringInput("Enter neighborhood: ");
                    filterController.updateNeighborhood(neighborhood);
                }
                case 2 -> {
                    String flatTypesStr = getStringInput("Enter flat types (comma-separated, e.g., 2-Room,3-Room): ");
                    List<String> flatTypes = Arrays.asList(flatTypesStr.split(","));
                    filterController.updateFlatTypes(flatTypes);
                }
                case 3 -> {
                    String openingAfterStr = getStringInput("Enter opening after date (yyyy-MM-dd): ");
                    try {
                        LocalDate openingAfter = LocalDate.parse(openingAfterStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        filterController.updateOpeningAfter(openingAfter);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format.");
                    }
                }
                case 4 -> {
                    String closingBeforeStr = getStringInput("Enter closing before date (yyyy-MM-dd): ");
                    try {
                        LocalDate closingBefore = LocalDate.parse(closingBeforeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        filterController.updateClosingBefore(closingBefore);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format.");
                    }
                }
                case 5 -> {
                    String manager = getStringInput("Enter manager name: ");
                    filterController.updateManager(manager);
                }
                case 6 -> {
                    String officer = getStringInput("Enter officer name: ");
                    filterController.updateOfficer(officer);
                }
                case 7 -> {
                    filterController.updateAllFilters();
                    System.out.println("All filters cleared.");
                }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    private String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    @Override
    public List<String> applyFilters(List<String> projectNames) {
        return filterController.applyFilters(projectNames);
    }
}