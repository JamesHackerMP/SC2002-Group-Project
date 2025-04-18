package boundary;

import boundary.interfaces.user.*;
import control.ApplicationController;
import control.AuthenticationController;
import control.EnquiryController;
import control.FilterController;
import control.ProjectController;
import entity.Application;
import entity.Enquiry;
import entity.Filter;
import entity.Project;
import entity.User;
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
    public User login() {
        System.out.println("\n=== Login ===");
        System.out.print("Enter NRIC: ");
        String nric = scanner.nextLine();

        if (authController.checkNric(nric) == false) {
            System.out.println("Invalid NRIC. Please try again.");
            return null;
        }
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();        

        User user = authController.authenticate(nric, password);
        if (user == null) {
            System.out.println("Invalid password. Please try again.");
            return null;
        }

        filterController.setCurrentUser(user);

        System.out.println("\nLogin successful! Welcome, " + user.getName() + " (" + user.getRole() + ")");
        
        displayNotifications(user);
        
        return user;
        }
        
        @Override
        public void displayNotifications(User user) {
            switch (user.getRole()) {
                case "Applicant" -> displayApplicantNotifications(user);
                case "HDBOfficer" -> displayOfficerNotifications(user);
                case "HDBManager" -> displayManagerNotifications(user);
            }
        }
        
        @Override
        public void displayApplicantNotifications(User user) {

            Application application = applicationController.getApplication(user.getName());
            if (application != null) {
                System.out.println("\nNOTIFICATION: Your application for project " + 
                                application.getProjectName() + " is " + application.getStatus());
            }
            
            List<Enquiry> answeredEnquiries = enquiryController.getEnquiriesByApplicant(user.getName()).stream()
                    .filter(e -> e.getAnswer() != null && !e.getAnswer().isEmpty())
                    .toList();
            
            if (!answeredEnquiries.isEmpty()) {
                System.out.println("\nNOTIFICATION: You have " + answeredEnquiries.size() + 
                                " answered enquiries. Check the Manage Enquiry menu to view responses.");
            }
            
            if (application == null && answeredEnquiries.isEmpty()) {
                System.out.println("\nNOTIFICATION: No current applications or enquiry responses.");
            }
        }
        
        @Override
        public void displayOfficerNotifications(User user) {
            Project currentProject = projectController.getCurrentProjectByOfficer(user.getName());
            if (currentProject != null) {
                long pendingBookings = applicationController.getAllApplications().stream()
                        .filter(a -> a.getProjectName().equals(currentProject.getName()) && 
                            a.getStatus() == Application.Status.SUCCESSFUL)
                        .count();
                
                long unansweredEnquiries = enquiryController.getEnquiriesByProject(currentProject.getName()).stream()
                        .filter(e -> e.getAnswer() == null)
                        .count();
                        
                if (pendingBookings > 0) {
                    System.out.println("\nNOTIFICATION: You have " + pendingBookings + 
                                    " approved applications waiting for booking.");
                }
                
                if (unansweredEnquiries > 0) {
                    System.out.println("\nNOTIFICATION: You have " + unansweredEnquiries + 
                                    " unanswered enquiries for project " + currentProject.getName() + ".");
                }
                
                if (pendingBookings == 0 && unansweredEnquiries == 0) {
                    System.out.println("\nNOTIFICATION: No pending applications or enquiries to process.");
                }
            }
        }
        
        @Override
        public void displayManagerNotifications(User user) {
            List<Project> managerProjects = projectController.getProjectsByManager(user.getName());
            if (!managerProjects.isEmpty()) {
                int totalPendingOfficers = 0;
                int totalPendingApplications = 0;
                int totalUnansweredEnquiries = 0;
                
                for (Project project : managerProjects) {
                    totalPendingOfficers += project.getPendingOfficers().size();
                    
                    long pendingApps = applicationController.getAllApplications().stream()
                            .filter(a -> a.getProjectName().equals(project.getName()) && 
                                a.getStatus() == Application.Status.PENDING)
                            .count();
                    totalPendingApplications += pendingApps;
                    
                    long unanswered = enquiryController.getEnquiriesByProject(project.getName()).stream()
                            .filter(e -> e.getAnswer() == null)
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
    public boolean changePassword(User user) {
        System.out.println("\n=== Change Password ===");
        System.out.print("Enter Current Password: ");
        String currentPassword = scanner.nextLine();
        System.out.print("Enter New Password: ");
        String newPassword = scanner.nextLine();

        boolean success = authController.changePassword(user.getNric(), currentPassword, newPassword);
        if (success) {
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password. Please check your current password.");
        }
        return success;
    }

    @Override
    public void displayMainMenu(User user) {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. View Profile");
        System.out.println("2. Change Password");
        System.out.println("3. Set Project Filters");
    
        switch (user.getRole()) {
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
    public void displayProfile(User user) {
        System.out.println("\n=== Your Profile ===");
        System.out.println("Name: " + user.getName());
        System.out.println("NRIC: " + user.getNric());
        System.out.println("Age: " + user.getAge());
        System.out.println("Marital Status: " + user.getMaritalStatus());
        System.out.println("Role: " + user.getRole());
    }

    @Override
    public void setFilters() {
        Filter filter = filterController.getFilter();
        while (true) {
            System.out.println("\n=== Set Project Filters ===");
            System.out.println("Current filters:");
            System.out.println("Neighborhood: " + (filter.getNeighborhood() != null ? filter.getNeighborhood() : "Not set"));
            System.out.println("Flat Types: " + (filter.getFlatTypes() != null ? filter.getFlatTypes() : "Not set"));
            System.out.println("Opening After: " + (filter.getOpeningAfter() != null ? filter.getOpeningAfter() : "Not set"));
            System.out.println("Closing Before: " + (filter.getClosingBefore() != null ? filter.getClosingBefore() : "Not set"));
            System.out.println("Manager: " + (filter.getManager() != null ? filter.getManager() : "Not set"));
            System.out.println("Officer: " + (filter.getOfficer() != null ? filter.getOfficer() : "Not set"));
    
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
                    filter.setNeighborhood(neighborhood);
                }
                case 2 -> {
                    String flatTypesStr = getStringInput("Enter flat types (comma-separated, e.g., 2-Room,3-Room): ");
                    List<String> flatTypes = Arrays.asList(flatTypesStr.split(","));
                    filter.setFlatTypes(flatTypes);
                }
                case 3 -> {
                    String openingAfterStr = getStringInput("Enter opening after date (yyyy-MM-dd): ");
                    try {
                        LocalDate openingAfter = LocalDate.parse(openingAfterStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        filter.setOpeningAfter(openingAfter);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format.");
                    }
                }
                case 4 -> {
                    String closingBeforeStr = getStringInput("Enter closing before date (yyyy-MM-dd): ");
                    try {
                        LocalDate closingBefore = LocalDate.parse(closingBeforeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        filter.setClosingBefore(closingBefore);
                    } catch (DateTimeParseException e) {
                        System.out.println("Invalid date format.");
                    }
                }
                case 5 -> {
                    String manager = getStringInput("Enter manager name: ");
                    filter.setManager(manager);
                }
                case 6 -> {
                    String officer = getStringInput("Enter officer name: ");
                    filter.setOfficer(officer);
                }
                case 7 -> {
                    filter.setNeighborhood(null);
                    filter.setFlatTypes(null);
                    filter.setOpeningAfter(null);
                    filter.setClosingBefore(null);
                    filter.setManager(null);
                    filter.setOfficer(null);
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
    public List<Project> applyFilters(List<Project> projects) {
        return filterController.applyFilters(projects);
    }
}