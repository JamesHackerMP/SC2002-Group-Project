package boundary;

import boundary.interfaces.manager.*;
import control.*;
import entity.*;
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
                        ManagerController managerController, AuthenticationController authController, FilterController filterController) {
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiryController = enquiryController;
        this.managerController = managerController;
        this.authController = authController;
        this.filterController = filterController;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu(HDBManager manager) {
        while (true) {
            System.out.println("\n=== HDB Manager Menu ===");
            System.out.println("1. Create New Project");
            System.out.println("2. Edit Project");
            System.out.println("3. Delete Project");
            System.out.println("4. Toggle Project Visibility");
            System.out.println("5. View All Projects");
            System.out.println("6. Manage Officer Registrations");
            System.out.println("7. Process Applications");
            System.out.println("8. Generate Reports");
            System.out.println("9. Manage Enquiries");
            System.out.println("0. Back to Main Menu");

            int choice = getMenuChoice();
            switch (choice) {
                case 1 -> createProject(manager);
                case 2 -> editProject(manager);
                case 3 -> deleteProject(manager);
                case 4 -> toggleVisibility(manager);
                case 5 -> viewAllProjects();
                case 6 -> handlePendingOfficers(manager);
                case 7 -> processApplications(manager);
                case 8 -> generateReports();
                case 9 -> manageEnquiries(manager);
                case 0 -> {return;}
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public void createProject(HDBManager manager) {
        if (projectController.getActiveProjectByManager(manager.getName()) != null) {
            System.out.println("You already have an active project.");
            return;
        }

        System.out.println("\n=== Create New Project ===");

        System.out.print("Project Name: ");
        String name = scanner.nextLine();

        if (projectController.getAllProjects().stream().anyMatch(p -> p.getName().equals(name))) {
            System.out.println("There is a project has the same name.");
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
        LocalDate openingDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        System.out.print("Application Closing Date (yyyy/MM/dd): ");
        LocalDate closingDate = LocalDate.parse(scanner.nextLine(), DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        System.out.print("Officer Slots (max 10): ");
        int officerSlots = Integer.parseInt(scanner.nextLine());
        officerSlots = Math.min(officerSlots, 10);

        Project newProject = new Project(name, neighborhood, twoRoomUnits, twoRoomPrice,
                threeRoomUnits, threeRoomPrice, openingDate,
                closingDate, manager.getName(), officerSlots);

        if (projectController.createProject(newProject)) {
            System.out.println("Project created successfully!");
        } else {
            System.out.println("Failed to create project. You might have a active project at that time.");
        }
    }

    @Override
    public void editProject(HDBManager manager) {
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getName());
    
        if (managerProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
    
        System.out.println("\n=== Your Projects ===");
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.printf("%d. %s (Neighborhood: %s, 2-Room Units: %d, 3-Room Units: %d)%n",
                    i + 1, project.getName(), project.getNeighborhood(),
                    project.getTwoRoomUnits(), project.getThreeRoomUnits());
        }
        System.out.println("0. Cancel");
    
        int projectChoice = -1;
        while (projectChoice < 0 || projectChoice > managerProjects.size()) {
            System.out.print("\nSelect a project to edit (Enter number) ");
            try {
                projectChoice = Integer.parseInt(scanner.nextLine());
                if (projectChoice == 0) {
                    System.out.println("Edit canceled.");
                    return;
                }
                if (projectChoice < 1 || projectChoice > managerProjects.size()) {
                    System.out.println("Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    
        Project selectedProject = managerProjects.get(projectChoice - 1);
    
        System.out.println("\n=== Editing Project: " + selectedProject.getName() + " ===");
        System.out.println("Current Neighborhood: " + selectedProject.getNeighborhood());
        System.out.print("Enter new neighborhood (or press Enter to keep current): ");
        String newNeighborhood = scanner.nextLine();
        if (!newNeighborhood.isBlank()) {
            selectedProject.setNeighborhood(newNeighborhood);
        }
    
        System.out.println("Current 2-Room Units: " + selectedProject.getTwoRoomUnits());
        System.out.print("Enter new number of 2-Room Units (or press Enter to keep current): ");
        String newTwoRoomUnits = scanner.nextLine();
        if (!newTwoRoomUnits.isBlank()) {
            try {
                selectedProject.setTwoRoomUnits(Integer.parseInt(newTwoRoomUnits));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Keeping current value.");
            }
        }
    
        System.out.println("Current 3-Room Units: " + selectedProject.getThreeRoomUnits());
        System.out.print("Enter new number of 3-Room Units (or press Enter to keep current): ");
        String newThreeRoomUnits = scanner.nextLine();
        if (!newThreeRoomUnits.isBlank()) {
            try {
                selectedProject.setThreeRoomUnits(Integer.parseInt(newThreeRoomUnits));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Keeping current value.");
            }
        }
    
        if (projectController.saveProjects()) {
            System.out.println("Project updated successfully.");
        } else {
            System.out.println("Failed to update project.");
        }
    }

    @Override
    public void deleteProject(HDBManager manager) {
        System.out.println("\n=== Your Projects ===");
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getName());

        if (managerProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }

        for (Project project : managerProjects) {
            System.out.println("\nProject Name: " + project.getName());
            System.out.println("Neighborhood: " + project.getNeighborhood());
        }
        System.out.println("0. Cancel");

        System.out.print("\nEnter project name to delete: ");
        String projectName = scanner.nextLine();

        if (projectName.equals("0")) return;

        if (projectController.deleteProject(projectName)) {
            System.out.println("Project deleted successfully!");
        } else {
            System.out.println("Failed to delete project.");
        }
    }

    @Override
    public void toggleVisibility(HDBManager manager) {
        System.out.println("\n=== Your Projects ===");
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getName());
    
        if (managerProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
    
        for (int i = 0; i < managerProjects.size(); i++) {
            Project project = managerProjects.get(i);
            System.out.printf("%d. %s (Current Status: %s)%n", 
                    i + 1, project.getName(), project.isVisible() ? "Visible" : "Hidden");
        }
        System.out.println("0. Cancel");
    
        int choice;
        while (true) {
            System.out.print("\nSelect a project to toggle visibility (Enter number) ");

            choice = getMenuChoice();
            
            if (choice == 0) return;
            
            if (choice < 1 || choice > managerProjects.size()) {
                System.out.println("Invalid selection. Please try again.");
            } else {
                break;
            }
        }
    
        Project selectedProject = managerProjects.get(choice - 1);
        Project activeProject = projectController.getActiveProjectByManager(manager.getName());
        boolean newVisibility = !selectedProject.isVisible();

        if (newVisibility && activeProject != null 
        && selectedProject.isOpenForApplication()) {
            System.out.println("Failed to update visibility. You cannot have two active project.");
            return;
            }

        managerController.toggleProjectVisibility(selectedProject.getName(), newVisibility);
        System.out.println("Project visibility set to: " + (newVisibility ? "Visible" : "Hidden"));

    }

    @Override
    public void viewAllProjects() {
        System.out.println("\n=== All BTO Projects ===");
    
        Filter filter = filterController.getFilter();
        System.out.println("Current Filters:");
        if (filter.getNeighborhood() != null) {
            System.out.println("Neighborhood: " + filter.getNeighborhood());
        }
        if (filter.getFlatTypes() != null && !filter.getFlatTypes().isEmpty()) {
            System.out.println("Flat Types: " + filter.getFlatTypes());
        }
        if (filter.getOpeningAfter() != null) {
            System.out.println("Opening After: " + filter.getOpeningAfter());
        }
        if (filter.getClosingBefore() != null) {
            System.out.println("Closing Before: " + filter.getClosingBefore());
        }
        if (filter.getManager() != null) {
            System.out.println("Manager: " + filter.getManager());
        }
        if (filter.getOfficer() != null) {
            System.out.println("Officer: " + filter.getOfficer());
        }
    
        List<Project> allProjects = filterController.applyFilters(projectController.getAllProjects());
    
        if (allProjects.isEmpty()) {
            System.out.println("No projects available with the current filters.");
            return;
        }
    
        for (Project project : allProjects) {
            System.out.println("\nProject Name: " + project.getName());
            System.out.println("Neighborhood: " + project.getNeighborhood());
            System.out.println("2-Room Units Available: " + project.getTwoRoomUnits());
            System.out.println("2-Room Price: " + project.getTwoRoomPrice());
            System.out.println("3-Room Units: " + project.getThreeRoomUnits());
            System.out.println("3-Room Price: " + project.getThreeRoomPrice());
            System.out.println("Opening Date: " + project.getOpeningDate());
            System.out.println("Closing Date: " + project.getClosingDate());
            System.out.println("Manager: " + project.getManager());
            System.out.println("Visibility: " + (project.isVisible() ? "ON" : "OFF"));
            System.out.println("----------------------------------------");
        }
    }

    @Override
    public void handlePendingOfficers(HDBManager manager) {
        List<Project> myProjects = projectController.getProjectsByManager(manager.getName());
        List<Project> projectsWithPendingOfficers = new ArrayList<>();
    
        System.out.println("\n=== Pending Officer Registrations ===");
        for (Project project : myProjects) {
            List<String> pending = project.getPendingOfficers();
            if (!pending.isEmpty()) {
                projectsWithPendingOfficers.add(project);
                System.out.println((projectsWithPendingOfficers.size()) + ". Project: " + project.getName());
                for (String officer : pending) {
                    System.out.println("   - " + officer);
                }
            }
        }
    
        if (projectsWithPendingOfficers.isEmpty()) {
            System.out.println("No pending officer applications found.");
            return;
        }
    
        int projectChoice;
        while (true) {
            System.out.print("\nSelect a project to process (Enter number) ");
            projectChoice = getMenuChoice();
            
            if (projectChoice == 0) return;
            
            if (projectChoice < 1 || projectChoice > projectsWithPendingOfficers.size()) {
                System.out.println("Invalid selection. Please try again.");
            } else {
                break;
            }
        }
    
        Project selectedProject = projectsWithPendingOfficers.get(projectChoice - 1);
        List<String> pendingOfficers = selectedProject.getPendingOfficers();
    
        System.out.println("\n=== Pending Officers for Project: " + selectedProject.getName() + " ===");
        for (int i = 0; i < pendingOfficers.size(); i++) {
            System.out.println((i + 1) + ". " + pendingOfficers.get(i));
        }
    
        int officerChoice;
        while (true) {
            System.out.print("\nSelect an officer to process (Enter number) ");
            officerChoice = getMenuChoice();
            
            if (officerChoice == 0) return;
            
            if (officerChoice < 1 || officerChoice > pendingOfficers.size()) {
                System.out.println("Invalid selection. Please try again.");
            } else {
                break;
            }
        }
    
        String selectedOfficer = pendingOfficers.get(officerChoice - 1);
    
        User officer = authController.getUser(selectedOfficer);
        if (officer != null) {
            System.out.println("\n=== Officer Details ===");
            System.out.println("Name: " + officer.getName());
            System.out.println("Age: " + officer.getAge());
            System.out.println("Marital Status: " + officer.getMaritalStatus());
        } else {
            System.out.println("Failed to retrieve officer details.");
            return;
        }
    
        System.out.println("\n1. Approve officer");
        System.out.println("2. Reject officer");
        System.out.println("0. Cancel");
        System.out.print("Enter your choice: ");
        int decision = getMenuChoice();
        
        boolean success = false;
        switch (decision) {
            case 1 -> {
                success = managerController.approveOfficer(selectedOfficer, selectedProject.getName());
                System.out.println(success ? "Officer approved successfully." : "Failed to approve officer.");
            }
            case 2 -> {
                success = managerController.rejectOfficer(selectedOfficer, selectedProject.getName());
                System.out.println(success ? "Officer rejected successfully." : "Failed to reject officer.");
            }
            case 0 -> System.out.println("Action canceled.");
            default -> System.out.println("Invalid choice. No action taken.");
        }
    }

    @Override
    public void processApplications(HDBManager manager) {
        System.out.println("\n=== Your Projects ===");
        List<Project> managerProjects = projectController.getProjectsByManager(manager.getName());
    
        if (managerProjects.isEmpty()) {
            System.out.println("You are not managing any projects.");
            return;
        }
    
        List<Project> projectsWithPendingActions = new ArrayList<>();
        for (Project project : managerProjects) {
            long pendingApplicationsCount = applicationController.getAllApplications().stream()
                    .filter(a -> a.getProjectName().equals(project.getName()) &&
                            a.getStatus() == Application.Status.PENDING)
                    .count();
    
            if (pendingApplicationsCount > 0) {
                projectsWithPendingActions.add(project);
                System.out.println("\nProject: " + project.getName());
                System.out.println("Pending Applications: " + pendingApplicationsCount);
            }
        }
    
        if (projectsWithPendingActions.isEmpty()) {
            System.out.println("No pending applications for your projects.");
            return;
        }
    
        System.out.println("\nSelect a project to process (Enter number)");
        for (int i = 0; i < projectsWithPendingActions.size(); i++) {
            System.out.println((i + 1) + ". " + projectsWithPendingActions.get(i).getName());
        }
        System.out.println("0. Cancel");
    
        int projectChoice;
        while (true) {
            System.out.print("Enter your choice: ");
            projectChoice = getMenuChoice();
            
            if (projectChoice == 0) return;
            
            if (projectChoice < 1 || projectChoice > projectsWithPendingActions.size()) {
                System.out.println("Invalid selection. Please try again.");
            } else {
                break;
            }
        }
    
        Project selectedProject = projectsWithPendingActions.get(projectChoice - 1);
        processPendingApplications(selectedProject);
    }
    
    @Override
    public void processPendingApplications(Project project) {
        System.out.println("\n=== Pending Applications for " + project.getName() + " ===");
        List<Application> pendingApps = applicationController.getAllApplications().stream()
                .filter(a -> a.getProjectName().equals(project.getName()) &&
                        a.getStatus() == Application.Status.PENDING)
                .toList();
    
        if (pendingApps.isEmpty()) {
            System.out.println("No pending applications for this project.");
            return;
        }
    
        for (int i = 0; i < pendingApps.size(); i++) {
            Application app = pendingApps.get(i);
            System.out.println((i + 1) + ". Applicant: " + app.getApplicantName());
        }
        System.out.println("0. Cancel");
    
        int appChoice;
        while (true) {
            System.out.print("Enter your choice: ");
            appChoice = getMenuChoice();
            
            if (appChoice == 0) return;
            
            if (appChoice < 1 || appChoice > pendingApps.size()) {
                System.out.println("Invalid selection. Please try again.");
            } else {
                break;
            }
        }
    
        Application selectedApp = pendingApps.get(appChoice - 1);
    
        User applicant = authController.getUser(selectedApp.getApplicantName());
        if (applicant != null) {
            System.out.println("\n=== Applicant Details ===");
            System.out.println("Name: " + applicant.getName());
            System.out.println("Age: " + applicant.getAge());
            System.out.println("Marital Status: " + applicant.getMaritalStatus());
            System.out.println("Flat Type Apply: " + selectedApp.getFlatTypeApply());
        } else {
            System.out.println("Failed to retrieve applicant details.");
            return;
        }
    
        System.out.println("\n1. Approve application");
        System.out.println("2. Reject application");
        System.out.println("0. Cancel");
        System.out.print("Enter your choice: ");
        int decision = getMenuChoice();
        
        switch (decision) {
            case 1 -> {
                if (applicationController.approveApplication(selectedApp.getApplicantName())) {
                    System.out.println("Application approved.");
                } else {
                    System.out.println("Failed to approve application.");
                }
            }
            case 2 -> {
                if (applicationController.rejectApplication(selectedApp.getApplicantName())) {
                    System.out.println("Application rejected.");
                } else {
                    System.out.println("Failed to reject application.");
                }
            }
            case 0 -> System.out.println("Action canceled.");
            default -> System.out.println("Invalid choice. No action taken.");
        }
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
    
                String maritalStatus = switch (maritalChoice) {
                    case 1 -> "Married";
                    case 2 -> "Single";
                    default -> null;
                };

                if (maritalStatus == null) {
                    System.out.println("Invalid choice.");
                    return;
                }
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
    
                String flatType = switch (flatTypeChoice) {
                    case 1 -> "2-Room";
                    case 2 -> "3-Room";
                    default -> null;
                };
                if (flatType == null) {
                    System.out.println("Invalid choice.");
                    return;
                }

                reportType = "Applicants by Flat Type: " + flatType;
                filters.put("flatType", flatType);
            }
            case 6 -> {
                List<Project> allProjects = projectController.getAllProjects();
                if (allProjects.isEmpty()) {
                    System.out.println("No projects available.");
                    return;
                }
    
                System.out.println("\nSelect a Project (Enter number)");
                for (int i = 0; i < allProjects.size(); i++) {
                    System.out.println((i + 1) + ". " + allProjects.get(i).getName());
                }
                System.out.println("0. Cancel");
    
                int projectChoice = getMenuChoice();
                if (projectChoice == 0) return;
    
                if (projectChoice < 1 || projectChoice > allProjects.size()) {
                    System.out.println("Invalid choice.");
                    return;
                }
    
                String projectName = allProjects.get(projectChoice - 1).getName();
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
    
        Report report = managerController.generateApplicationsReport(filters);
    
        System.out.println("\n=== " + reportType + " Report ===");
        System.out.println("Generated on: " + report.getGeneratedDate().toLocalDate());
    
        if (report.getData().isEmpty()) {
            System.out.println("No data found for the selected report type.");
            return;
        }
    
        for (String[] row : report.getData()) {
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
    public void manageEnquiries(HDBManager manager) {
        String managerName = manager.getName();
        List<Project> managerProjects = projectController.getProjectsByManager(managerName);
        List<String> managedProjectNames = managerProjects.stream()
                                            .map(Project::getName)
                                            .toList();
        
        System.out.println("\n=== All Enquiries ===");
        List<Enquiry> allEnquiries = enquiryController.getAllEnquiries();
        
        if (allEnquiries.isEmpty()) {
            System.out.println("No enquiries found in the system.");
            return;
        }
        
        System.out.println("\n--- Other Projects' Enquiries (View Only) ---");
        boolean hasOtherEnquiries = false;
        
        for (int i = 0; i < allEnquiries.size(); i++) {
            Enquiry enquiry = allEnquiries.get(i);
            
            if (managedProjectNames.contains(enquiry.getProjectName())) {
                continue;
            }
            
            hasOtherEnquiries = true;
            System.out.println((i + 1) + ". Project: " + enquiry.getProjectName() + " [View Only]");
            System.out.println("   ID: " + enquiry.getId());
            System.out.println("   Applicant: " + enquiry.getApplicantName());
            System.out.println("   Question: " + enquiry.getQuestion());
            System.out.println("   Posted: " + enquiry.getCreatedDate());
            if (enquiry.getAnswer() != null) {
                System.out.println("   Answer: " + enquiry.getAnswer());
                System.out.println("   Answered: " + enquiry.getAnsweredDate());
                System.out.println("   Status: Answered");
            } else {
                System.out.println("   Status: Pending response");
            }
            System.out.println();
        }
        
        if (!hasOtherEnquiries) {
            System.out.println("No enquiries from other projects.");
        }
        
        System.out.println("\n--- Your Projects' Enquiries (Can Reply) ---");
        boolean hasOwnEnquiries = false;
        
        for (int i = 0; i < allEnquiries.size(); i++) {
            Enquiry enquiry = allEnquiries.get(i);
            
            if (!managedProjectNames.contains(enquiry.getProjectName())) {
                continue;
            }
            
            hasOwnEnquiries = true;
            System.out.println((i + 1) + ". Project: " + enquiry.getProjectName() + " [Can Reply]");
            System.out.println("   ID: " + enquiry.getId());
            System.out.println("   Applicant: " + enquiry.getApplicantName());
            System.out.println("   Question: " + enquiry.getQuestion());
            System.out.println("   Posted: " + enquiry.getCreatedDate());
            if (enquiry.getAnswer() != null) {
                System.out.println("   Answer: " + enquiry.getAnswer());
                System.out.println("   Answered: " + enquiry.getAnsweredDate());
                System.out.println("   Status: Answered");
            } else {
                System.out.println("   Status: Pending response");
            }
            System.out.println();
        }
        
        if (!hasOwnEnquiries) {
            System.out.println("No enquiries for your projects.");
        }
        
        System.out.println("0. Back");
        
        int choice;
        while (true) {
            System.out.print("\nSelect an enquiry to reply (Enter number) ");
            choice = getMenuChoice();
            
            if (choice == 0) return;
            
            if (choice < 1 || choice > allEnquiries.size()) {
                System.out.println("Invalid selection. Please try again.");
                continue;
            }
            
            Enquiry selectedEnquiry = allEnquiries.get(choice - 1);
            
            if (!managedProjectNames.contains(selectedEnquiry.getProjectName())) {
                System.out.println("You can only reply to enquiries for projects you manage.");
                continue;
            }
            
            if (selectedEnquiry.getAnswer() != null) {
                System.out.println("This enquiry has already been answered.");
                System.out.println("Current answer: " + selectedEnquiry.getAnswer());
                System.out.print("Do you want to update the answer? (Y/N): ");
                String updateDecision = scanner.nextLine().trim().toUpperCase();
                if (!updateDecision.equals("Y")) {
                    continue;
                }
            }
            
            System.out.println("\nQuestion: " + selectedEnquiry.getQuestion());
            System.out.print("Enter your reply: ");
            String answer = scanner.nextLine();
            
            if (enquiryController.replyToEnquiry(selectedEnquiry.getId(), answer)) {
                System.out.println("Reply submitted successfully.");
            } else {
                System.out.println("Failed to submit reply.");
            }
            
            break;
        }
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