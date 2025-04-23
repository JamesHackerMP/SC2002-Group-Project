package boundary;

import boundary.interfaces.manager.*;
import control.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HDBManagerUI implements ProjectManagementManagerUI, OfficerManagementUI, 
                                    ApplicationProcessingUI, ReportGenerationUI, 
                                    EnquiryManagerUI, ManagerMenuUI {
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiryController enquiryController;
    private final ManagerController managerController;
    private final AuthenticationController authController;
    private final FilterController filterController;
    private final Scanner scanner;

    public HDBManagerUI(ProjectController projectController,
                        ApplicationController applicationController,
                        EnquiryController enquiryController,
                        ManagerController managerController, 
                        AuthenticationController authController, 
                        FilterController filterController) {
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiryController = enquiryController;
        this.managerController = managerController;
        this.authController = authController;
        this.filterController = filterController;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu(String managerName) {
        displayManagerNotifications(managerName);
        while (true) {
            System.out.println("\n=== HDB Manager Menu ===");
            System.out.println("1. View All Projects");
            System.out.println("2. Manage Projects");
            System.out.println("3. Manage Officer Registrations");
            System.out.println("4. Process Applications");
            System.out.println("5. Manage Enquiries");
            System.out.println("6. Generate Reports");
            System.out.println("0. Back to Main Menu");
    
            int choice = getMenuChoice();
            switch (choice) {
                case 1 -> viewAllProjects();
                case 2 -> manageProjects(managerName);
                case 3 -> handlePendingOfficers(managerName);
                case 4 -> processApplications(managerName);
                case 5 -> manageEnquiries(managerName);
                case 6 -> generateReports();
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void displayManagerNotifications(String managerName) {
        List<String> managerProjectNames = projectController.getProjectsByManager(managerName);
        if (!managerProjectNames.isEmpty()) {
            int totalPendingOfficers = 0;
            int totalPendingApplications = 0;
            int totalUnansweredEnquiries = 0;
            
            for (String projectName : managerProjectNames) {
                totalPendingOfficers += projectController.checkPendingOfficers(projectName).size();
                
                long pendingApps = applicationController.getAllApplications().stream()
                        .filter(a -> applicationController.checkProjectName(a).equals(projectName) && 
                                    applicationController.isStatusPending(a))
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
    public void manageProjects(String managerName) {
        while (true) {
            System.out.println("\n=== Manage Projects ===");
            System.out.println("1. Create New Project");
            System.out.println("2. Edit Project");
            System.out.println("3. Delete Project");
            System.out.println("4. Toggle Project Visibility");
            System.out.println("0. Back to Main Menu");
    
            int choice = getMenuChoice();
            switch (choice) {
                case 1 -> createProject(managerName);
                case 2 -> editProject(managerName);
                case 3 -> deleteProject(managerName);
                case 4 -> toggleVisibility(managerName);
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public void createProject(String managerName) {
        if (projectController.getActiveProjectByManager(managerName) != null) {
            System.out.println("You already have an active project.");
            return;
        }

        System.out.println("\n=== Create New Project ===");

        System.out.print("Project Name: ");
        String name = scanner.nextLine();

        if (projectController.getProject(name) != null) {
            System.out.println("There is a project with the same name.");
            return;
        }

        System.out.print("Neighborhood: ");
        String neighborhood = scanner.nextLine();

        System.out.print("Number of 2-Room Units (0 if none): ");
        int twoRoomUnits = Integer.parseInt(scanner.nextLine());
        
        int twoRoomPrice = 0;
        if (twoRoomUnits > 0) {
            System.out.print("Price per 2-Room Unit: ");
            twoRoomPrice = Integer.parseInt(scanner.nextLine());
        }
        
        System.out.print("Number of 3-Room Units (0 if none): ");
        int threeRoomUnits = Integer.parseInt(scanner.nextLine());
        
        int threeRoomPrice = 0;
        if (threeRoomUnits > 0) {
            System.out.print("Price per 3-Room Unit: ");
            threeRoomPrice = Integer.parseInt(scanner.nextLine());
        }

        System.out.print("Application Opening Date (yyyy/MM/dd): ");
        String openingDateStr = scanner.nextLine();
        LocalDate openingDate = LocalDate.parse(openingDateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        System.out.print("Application Closing Date (yyyy/MM/dd): ");
        String closingDateStr = scanner.nextLine();
        LocalDate closingDate = LocalDate.parse(closingDateStr, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        System.out.print("Officer Slots (max 10): ");
        int officerSlots = Integer.parseInt(scanner.nextLine());
        officerSlots = Math.min(officerSlots, 10);

        boolean success = projectController.createProject(name, neighborhood, twoRoomUnits, twoRoomPrice,
                threeRoomUnits, threeRoomPrice, openingDate, closingDate, managerName, officerSlots);

        if (success) {
            System.out.println("Project created successfully!");
        } else {
            System.out.println("Failed to create project. You might have an active project at that time.");
        }
    }

    @Override
    public void editProject(String managerName) {
        List<String> projectNames = projectController.getProjectsByManager(managerName);
    
        if (projectNames.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
    
        System.out.println("\n=== Your Projects ===");
        for (int i = 0; i < projectNames.size(); i++) {
            String projectName = projectNames.get(i);
            System.out.printf("%d. %s (Neighborhood: %s, 2-Room Units: %d, 3-Room Units: %d)%n",
                    i + 1, projectName, projectController.checkNeighborhood(projectName),
                    projectController.checkTwoRoomUnits(projectName), projectController.checkThreeRoomUnits(projectName));
        }
        System.out.println("0. Cancel");
    
        int projectChoice = getMenuChoice();
        if (projectChoice == 0) {
            System.out.println("Edit canceled.");
            return;
        }
        if (projectChoice < 1 || projectChoice > projectNames.size()) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }
    
        String selectedProjectName = projectNames.get(projectChoice - 1);
    
        System.out.println("\n=== Editing Project: " + selectedProjectName + " ===");
        System.out.println("Current Neighborhood: " + projectController.checkNeighborhood(selectedProjectName));
        System.out.print("Enter new neighborhood (or press Enter to keep current): ");
        String newNeighborhood = scanner.nextLine();
        if (!newNeighborhood.isBlank()) {
            projectController.updateNeighborhood(selectedProjectName, newNeighborhood);
        }
    
        System.out.println("Current 2-Room Units: " + projectController.checkTwoRoomUnits(selectedProjectName));
        System.out.print("Enter new number of 2-Room Units (or press Enter to keep current): ");
        String newTwoRoomUnits = scanner.nextLine();
        if (!newTwoRoomUnits.isBlank()) {
            try {
                projectController.updateTwoRoomUnits(selectedProjectName, Integer.parseInt(newTwoRoomUnits));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Keeping current value.");
            }
        }
    
        System.out.println("Current 3-Room Units: " + projectController.checkThreeRoomUnits(selectedProjectName));
        System.out.print("Enter new number of 3-Room Units (or press Enter to keep current): ");
        String newThreeRoomUnits = scanner.nextLine();
        if (!newThreeRoomUnits.isBlank()) {
            try {
                projectController.updateThreeRoomUnits(selectedProjectName, Integer.parseInt(newThreeRoomUnits));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Keeping current value.");
            }
        }
    
        System.out.println("Project updated successfully.");
    }

    @Override
    public void deleteProject(String managerName) {
        List<String> projectNames = projectController.getProjectsByManager(managerName);

        if (projectNames.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }

        System.out.println("\n=== Your Projects ===");
        for (String projectName : projectNames) {
            System.out.println("\nProject Name: " + projectName);
            System.out.println("Neighborhood: " + projectController.checkNeighborhood(projectName));
        }
        System.out.println("0. Cancel");

        System.out.print("\nEnter project name to delete: ");
        String projectName = scanner.nextLine();

        if (projectName.equals("0")) {
            return;
        }

        if (projectController.deleteProject(projectName)) {
            System.out.println("Project deleted successfully!");
        } else {
            System.out.println("Failed to delete project.");
        }
    }

    @Override
    public void toggleVisibility(String managerName) {
        List<String> projectNames = projectController.getProjectsByManager(managerName);
    
        if (projectNames.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
    
        System.out.println("\n=== Your Projects ===");
        for (int i = 0; i < projectNames.size(); i++) {
            String projectName = projectNames.get(i);
            System.out.printf("%d. %s (Current Status: %s)%n", 
                    i + 1, projectName, projectController.checkVisible(projectName) ? "Visible" : "Hidden");
        }
        System.out.println("0. Cancel");
    
        int choice = getMenuChoice();
        if (choice == 0) {
            System.out.println("Action canceled.");
            return;
        }
        if (choice < 1 || choice > projectNames.size()) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }
    
        String selectedProjectName = projectNames.get(choice - 1);
        boolean currentVisibility = projectController.checkVisible(selectedProjectName);
        boolean newVisibility = !currentVisibility;

        if (newVisibility && projectController.getActiveProjectByManager(managerName) != null) {
            System.out.println("Failed to update visibility. You cannot have two active projects.");
            return;
        }

        managerController.toggleProjectVisibility(selectedProjectName, newVisibility);
        System.out.println("Project visibility set to: " + (newVisibility ? "Visible" : "Hidden"));
    }

    @Override
    public void viewAllProjects() {
        System.out.println("\n=== All BTO Projects ===");
    
        System.out.println("Current Filters:");
        if (filterController.checkNeighborhood() != null) {
            System.out.println("Neighborhood: " + filterController.checkNeighborhood());
        }
        if (filterController.checkFlatTypes() != null && !filterController.checkFlatTypes().isEmpty()) {
            System.out.println("Flat Types: " + filterController.checkFlatTypes());
        }
        if (filterController.checkOpeningAfter() != null) {
            System.out.println("Opening After: " + filterController.checkOpeningAfter());
        }
        if (filterController.checkClosingBefore() != null) {
            System.out.println("Closing Before: " + filterController.checkClosingBefore());
        }
        if (filterController.checkManager() != null) {
            System.out.println("Manager: " + filterController.checkManager());
        }
        if (filterController.checkOfficer() != null) {
            System.out.println("Officer: " + filterController.checkOfficer());
        }
    
        List<String> projectNames = filterController.applyFilters(projectController.getAllProjects());
    
        if (projectNames.isEmpty()) {
            System.out.println("No projects available with the current filters.");
            return;
        }
    
        for (String projectName : projectNames) {
            System.out.println("\nProject Name: " + projectName);
            System.out.println("Neighborhood: " + projectController.checkNeighborhood(projectName));
            System.out.println("2-Room Units Available: " + projectController.checkTwoRoomUnits(projectName));
            System.out.println("2-Room Price: " + projectController.checkTwoRoomPrice(projectName));
            System.out.println("3-Room Units: " + projectController.checkThreeRoomUnits(projectName));
            System.out.println("3-Room Price: " + projectController.checkThreeRoomPrice(projectName));
            System.out.println("Opening Date: " + projectController.checkOpeningDate(projectName));
            System.out.println("Closing Date: " + projectController.checkClosingDate(projectName));
            System.out.println("Manager: " + projectController.checkManager(projectName));
            System.out.println("Visibility: " + (projectController.checkVisible(projectName) ? "ON" : "OFF"));
            System.out.println("----------------------------------------");
        }
    }

    @Override
    public void handlePendingOfficers(String managerName) {
        List<String> projectNames = projectController.getProjectsByManager(managerName);
        List<String> projectsWithPendingOfficers = new ArrayList<>();
    
        System.out.println("\n=== Pending Officer Registrations ===");
        for (String projectName : projectNames) {
            List<String> pendingOfficers = projectController.checkPendingOfficers(projectName);
            if (!pendingOfficers.isEmpty()) {
                projectsWithPendingOfficers.add(projectName);
                System.out.println((projectsWithPendingOfficers.size()) + ". Project: " + projectName);
                for (String officer : pendingOfficers) {
                    System.out.println("   - " + officer);
                }
            }
        }
    
        if (projectsWithPendingOfficers.isEmpty()) {
            System.out.println("No pending officer applications found.");
            return;
        }
    
        int projectChoice = getMenuChoice();
        if (projectChoice == 0) {
            return;
        }
        if (projectChoice < 1 || projectChoice > projectsWithPendingOfficers.size()) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }
    
        String selectedProjectName = projectsWithPendingOfficers.get(projectChoice - 1);
        List<String> pendingOfficers = projectController.checkPendingOfficers(selectedProjectName);
    
        System.out.println("\n=== Pending Officers for Project: " + selectedProjectName + " ===");
        for (int i = 0; i < pendingOfficers.size(); i++) {
            System.out.println((i + 1) + ". " + pendingOfficers.get(i));
        }
    
        int officerChoice = getMenuChoice();
        if (officerChoice == 0) {
            return;
        }
        if (officerChoice < 1 || officerChoice > pendingOfficers.size()) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }
    
        String selectedOfficer = pendingOfficers.get(officerChoice - 1);
    
        if (authController.getUser(selectedOfficer) != null) {
            System.out.println("\n=== Officer Details ===");
            System.out.println("Name: " + selectedOfficer);
            System.out.println("Age: " + authController.checkAge(selectedOfficer));
            System.out.println("Marital Status: " + authController.checkMaritalStatus(selectedOfficer));
        } else {
            System.out.println("Failed to retrieve officer details.");
            return;
        }
    
        System.out.println("\n1. Approve officer");
        System.out.println("2. Reject officer");
        System.out.println("0. Cancel");
        int decision = getMenuChoice();
        
        if (decision == 0) {
            System.out.println("Action canceled.");
            return;
        }
        
        if (decision != 1 && decision != 2) {
            System.out.println("Invalid choice. No action taken.");
            return;
        }
        
        String action = (decision == 1) ? "approve" : "reject";
        System.out.println("\nAre you sure you want to " + action + " officer " 
                         + selectedOfficer + " for project '" + selectedProjectName + "'?");
        System.out.println("1. Yes, confirm " + action);
        System.out.println("0. No, cancel");
        
        int confirmChoice = getMenuChoice();
        if (confirmChoice != 1) {
            System.out.println("Action cancelled.");
            return;
        }
        
        boolean success = (decision == 1) ? 
            managerController.approveOfficer(selectedOfficer, selectedProjectName) :
            managerController.rejectOfficer(selectedOfficer, selectedProjectName);
        System.out.println(success ? "Officer " + action + "d successfully." : "Failed to " + action + " officer.");
    }

    @Override
    public void processApplications(String managerName) {
        List<String> projectNames = projectController.getProjectsByManager(managerName);
    
        if (projectNames.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
    
        List<String> projectsWithPendingActions = new ArrayList<>();
        int pendingCount = 0;
        System.out.println("\n=== Your Projects ===");
        for (String projectName : projectNames) {
            long pendingApps = applicationController.getAllApplications().stream()
                            .filter(a -> {
                                return applicationController.checkProjectName(a).equals(projectName) && 
                                applicationController.isStatusPending(a);
                            })
                            .count();
                            pendingCount += pendingApps;
            if (pendingCount > 0) {
                projectsWithPendingActions.add(projectName);
                System.out.println("\nProject: " + projectName);
                System.out.println("Pending Applications: " + pendingCount);
            }
        }
    
        if (projectsWithPendingActions.isEmpty()) {
            System.out.println("No pending applications for your projects.");
            return;
        }
    
        System.out.println("\nSelect a project to process (Enter number)");
        for (int i = 0; i < projectsWithPendingActions.size(); i++) {
            System.out.println((i + 1) + ". " + projectsWithPendingActions.get(i));
        }
        System.out.println("0. Cancel");
    
        int projectChoice = getMenuChoice();
        if (projectChoice == 0) {
            return;
        }
        if (projectChoice < 1 || projectChoice > projectsWithPendingActions.size()) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }
    
        String selectedProjectName = projectsWithPendingActions.get(projectChoice - 1);
        processPendingApplications(selectedProjectName);
    }
    
    @Override
    public void processPendingApplications(String projectName) {
        System.out.println("\n=== Pending Applications for " + projectName + " ===");

        List<String> applicantNames = applicationController.getAllApplications().stream()
                .filter(a -> applicationController.checkProjectName(a).equals(projectName) &&
                    applicationController.isStatusPending(a))
                .toList();
    
        if (applicantNames.isEmpty()) {
            System.out.println("No pending applications for this project.");
            return;
        }
    
        for (int i = 0; i < applicantNames.size(); i++) {
            System.out.println((i + 1) + ". Applicant: " + applicantNames.get(i));
        }
        System.out.println("0. Cancel");
    
        int appChoice = getMenuChoice();
        if (appChoice == 0) {
            return;
        }
        if (appChoice < 1 || appChoice > applicantNames.size()) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }
    
        String selectedApplicant = applicantNames.get(appChoice - 1);
    
        if (authController.getUser(selectedApplicant) != null) {
            System.out.println("\n=== Applicant Details ===");
            System.out.println("Name: " + selectedApplicant);
            System.out.println("Age: " + authController.checkAge(selectedApplicant));
            System.out.println("Marital Status: " + authController.checkMaritalStatus(selectedApplicant));
            System.out.println("Flat Type Apply: " + applicationController.checkFlatTypeApply(selectedApplicant));
        } else {
            System.out.println("Failed to retrieve applicant details.");
            return;
        }
    
        System.out.println("\n1. Approve application");
        System.out.println("2. Reject application");
        System.out.println("0. Cancel");
        int decision = getMenuChoice();
        
        if (decision == 0) {
            System.out.println("Action canceled.");
            return;
        }
        
        if (decision != 1 && decision != 2) {
            System.out.println("Invalid choice. No action taken.");
            return;
        }
        
        String action = (decision == 1) ? "approve" : "reject";
        System.out.println("\nAre you sure you want to " + action + " the application for " 
                         + selectedApplicant + "?");
        System.out.println("1. Yes, confirm " + action);
        System.out.println("0. No, cancel");
        
        int confirmChoice = getMenuChoice();
        if (confirmChoice != 1) {
            System.out.println("Action cancelled.");
            return;
        }
        
        boolean success = (decision == 1) ? 
            applicationController.approveApplication(selectedApplicant) :
            applicationController.rejectApplication(selectedApplicant);
        System.out.println(success ? "Application " + action + "d." : "Failed to " + action + " application.");
    }

    @Override
    public void generateReports() {
        System.out.println("\n=== Report Generation ===");
        System.out.println("1. All Applications");
        System.out.println("2. Successful Applications");
        System.out.println("3. Booked Flats");
        System.out.println("4. Filter by Marital Status");
        System.out.println("5. Filter by Flat Type");
        System.out.println("6. Filter by Project Name");
        System.out.println("7. Filter by Age Range");
        System.out.println("0. Cancel");
    
        int choice = getMenuChoice();
        if (choice == 0) return;
    
        Map<String, String> filters = new HashMap<>();
        String reportType;
    
        switch (choice) {
            case 1 -> reportType = "All Applications";
            case 2 -> {
                reportType = "Successful Applications";
                filters.put("status", "SUCCESSFUL");
            }
            case 3 -> {
                reportType = "Booked Flats";
                filters.put("status", "BOOKED");
            }
            case 4 -> {
                System.out.println("\nSelect Marital Status (Enter number)");
                System.out.println("1. Married");
                System.out.println("2. Single");
                System.out.println("0. Cancel");
                int maritalChoice = getMenuChoice();
                if (maritalChoice == 0) return;
    
                String maritalStatus = maritalChoice == 1 ? "Married" : "Single";
                reportType = "Applicants by Marital Status: " + maritalStatus;
                filters.put("maritalStatus", maritalStatus);
            }
            case 5 -> {
                System.out.println("\nSelect Flat Type (Enter number)");
                System.out.println("1. 2-Room");
                System.out.println("2. 3-Room");
                System.out.println("0. Cancel");
                int flatTypeChoice = getMenuChoice();
                if (flatTypeChoice == 0) return;
    
                String flatType = flatTypeChoice == 1 ? "2-Room" : "3-Room";
                reportType = "Applicants by Flat Type: " + flatType;
                filters.put("flatType", flatType);
            }
            case 6 -> {
                List<String> projectNames = projectController.getAllProjects();
                if (projectNames.isEmpty()) {
                    System.out.println("No projects available.");
                    return;
                }
    
                System.out.println("\nSelect a Project (Enter number)");
                for (int i = 0; i < projectNames.size(); i++) {
                    System.out.println((i + 1) + ". " + projectNames.get(i));
                }
                System.out.println("0. Cancel");
    
                int projectChoice = getMenuChoice();
                if (projectChoice == 0) return;
                if (projectChoice < 1 || projectChoice > projectNames.size()) {
                    System.out.println("Invalid choice.");
                    return;
                }
    
                String projectName = projectNames.get(projectChoice - 1);
                reportType = "Applicants by Project Name: " + projectName;
                filters.put("projectName", projectName);
            }
            case 7 -> {
                System.out.print("Enter minimum age: ");
                int minAge = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter maximum age: ");
                int maxAge = Integer.parseInt(scanner.nextLine());
                reportType = "Applicants by Age Range: " + minAge + " - " + maxAge;
                filters.put("minAge", String.valueOf(minAge));
                filters.put("maxAge", String.valueOf(maxAge));
            }
            default -> {
                System.out.println("Invalid choice.");
                return;
            }
        }
    
        boolean success = managerController.generateApplicationsReport(filters);
    
        if (!success) {
            System.out.println("Failed to generate report.");
            return;
        }
        
        System.out.println("\n=== " + reportType + " Report ===");
        System.out.println("Generated on: " + managerController.getReportGeneratedDate().toLocalDate());
        
        if (!managerController.hasReportData()) {
            System.out.println("No data found for the selected report type.");
            return;
        }
        
        for (String[] row : managerController.getReportData()) {
            System.out.println("\nApplicant: " + row[0]);
            System.out.println("Project: " + row[1]);
            System.out.println("Status: " + row[2]);
            if (row.length > 3 && row[3] != null && !row[3].equals("N/A")) {
                System.out.println("Flat Type: " + row[3]);
            }
            if (row.length > 4 && row[4] != null) {
                System.out.println("Age: " + row[4]);
            }
            if (row.length > 5 && row[5] != null) {
                System.out.println("Marital Status: " + row[5]);
            }
        }
    }

    @Override
    public void manageEnquiries(String managerName) {
        List<String> projectNames = projectController.getProjectsByManager(managerName);
        List<String> enquiryIds = enquiryController.getAllEnquiries();
        
        if (enquiryIds.isEmpty()) {
            System.out.println("No enquiries found in the system.");
            return;
        }
        
        System.out.println("\n--- Other Projects' Enquiries (View Only) ---");
        boolean hasOtherEnquiries = false;
        for (int i = 0; i < enquiryIds.size(); i++) {
            String enquiryId = enquiryIds.get(i);
            String projectName = enquiryController.checkProjectName(enquiryId);
            if (!projectNames.contains(projectName)) {
                hasOtherEnquiries = true;
                displayEnquiry(i, enquiryId, false);
            }
        }
        if (!hasOtherEnquiries) {
            System.out.println("No enquiries from other projects.");
        }
        
        System.out.println("\n--- Your Projects' Enquiries (Can Reply) ---");
        boolean hasOwnEnquiries = false;
        for (int i = 0; i < enquiryIds.size(); i++) {
            String enquiryId = enquiryIds.get(i);
            String projectName = enquiryController.checkProjectName(enquiryId);
            if (projectNames.contains(projectName)) {
                hasOwnEnquiries = true;
                displayEnquiry(i, enquiryId, true);
            }
        }
        if (!hasOwnEnquiries) {
            System.out.println("No enquiries for your projects.");
        }
        
        System.out.println("0. Back");
        
        int choice = getMenuChoice();
        if (choice == 0) {
            return;
        }
        if (choice < 1 || choice > enquiryIds.size()) {
            System.out.println("Invalid selection. Please try again.");
            return;
        }
        
        String selectedEnquiryId = enquiryIds.get(choice - 1);
        String selectedProjectName = enquiryController.checkProjectName(selectedEnquiryId);
        
        if (!projectNames.contains(selectedProjectName)) {
            System.out.println("You can only reply to enquiries for projects you manage.");
            return;
        }
        
        String currentAnswer = enquiryController.checkAnswer(selectedEnquiryId);
        if (currentAnswer != null) {
            System.out.println("This enquiry has already been answered.");
            System.out.println("Current answer: " + currentAnswer);
            System.out.println("1. Update answer");
            System.out.println("0. Cancel");
            int updateDecision = getMenuChoice();
            if (updateDecision != 1) {
                return;
            }
        }
        
        System.out.println("\nQuestion: " + enquiryController.checkQuestion(selectedEnquiryId));
        System.out.print("Enter your reply: ");
        String answer = scanner.nextLine();
        
        if (enquiryController.replyToEnquiry(selectedEnquiryId, answer)) {
            System.out.println("Reply submitted successfully.");
        } else {
            System.out.println("Failed to submit reply.");
        }
    }

    @Override
    public void displayEnquiry(int index, String enquiryId, boolean canReply) {
        System.out.println((index + 1) + ". Project: " + enquiryController.checkProjectName(enquiryId) + 
                (canReply ? " [Can Reply]" : " [View Only]"));
        System.out.println("   ID: " + enquiryId);
        System.out.println("   Applicant: " + enquiryController.checkApplicantName(enquiryId));
        System.out.println("   Question: " + enquiryController.checkQuestion(enquiryId));
        System.out.println("   Posted: " + enquiryController.checkFormattedCreatedDate(enquiryId));
        String answer = enquiryController.checkAnswer(enquiryId);
        if (answer != null) {
            System.out.println("   Answer: " + answer);
            System.out.println("   Answered: " + enquiryController.checkFormattedAnsweredDate(enquiryId));
            System.out.println("   Status: Answered");
        } else {
            System.out.println("   Status: Pending response");
        }
        System.out.println();
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
}