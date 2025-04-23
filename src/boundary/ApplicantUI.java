package boundary;

import boundary.interfaces.applicant.*;
import control.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplicantUI implements ProjectViewUI, ApplicationManagementUI, 
                                    EnquiryApplicantUI, ApplicantMenuUI {
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiryController enquiryController;
    private final AuthenticationController authController;
    private final FilterController filterController;
    private final Scanner scanner;

    public ApplicantUI(ProjectController projectController,
                       ApplicationController applicationController,
                       EnquiryController enquiryController, 
                       AuthenticationController authController,
                       FilterController filterController) {
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiryController = enquiryController;
        this.authController = authController;
        this.filterController = filterController;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu(String userName) {
        while (true) {
            System.out.println("\n=== Applicant Menu ===");
            System.out.println("1. View Available Projects");
            System.out.println("2. Apply for a Project");
            System.out.println("3. View My Application");
            System.out.println("4. Withdraw Application");
            System.out.println("5. Manage Enquiry");
            System.out.println("0. Back to Main Menu");
    
            int choice = getMenuChoice();
            switch (choice) {
                case 1 -> viewAvailableProjects(userName);
                case 2 -> applyForProject(userName);
                case 3 -> viewMyApplication(userName);
                case 4 -> withdrawApplication(userName);
                case 5 -> displayEnquiryMenu(userName);
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public void viewAvailableProjects(String userName) {
        System.out.println("\n=== Available Projects ===");
        
        List<String> projectNames = filterController.applyFilters(projectController.getVisibleProjects());
    
        projectNames.removeIf(projectName -> !applicationController.isEligibleForProject(userName, projectName));

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
    
        if (projectNames.isEmpty()) {
            System.out.println("No projects available with the current filters.");
            return;
        }
        
        for (String projectName : projectNames) {
        System.out.println("\nProject Name: " + projectName);
        System.out.println("Neighborhood: " + projectController.checkNeighborhood(projectName));
        
        if (projectController.checkHasTwoRoomUnits(projectName)) {
            System.out.println("2-Room Units Available: " + projectController.checkTwoRoomUnits(projectName));
            System.out.println("2-Room Price: " + projectController.checkTwoRoomPrice(projectName));
        }
        
        if (projectController.checkHasThreeRoomUnits(projectName) && 
            authController.checkMaritalStatus(userName).equalsIgnoreCase("Married")) {
            System.out.println("3-Room Units Available: " + projectController.checkThreeRoomUnits(projectName));
            System.out.println("3-Room Price: " + projectController.checkThreeRoomPrice(projectName));
        }
        
        System.out.println("Application Period: " + 
            projectController.checkOpeningDate(projectName) + " to " + 
            projectController.checkClosingDate(projectName));
        
        System.out.println("Manager: " + projectController.checkManager(projectName));
        System.out.println("Officers: " + projectController.checkOfficers(projectName));
        }
    }

    @Override
    public void applyForProject(String userName) {
        List<String> projectNames = projectController.getVisibleProjects();
    
        projectNames.removeIf(projectName -> !applicationController.isEligibleForProject(userName, projectName));
    
        if (projectNames.isEmpty()) {
            System.out.println("No projects available for application.");
            return;
        }
    
        System.out.println("\n=== Available Projects ===");
        for (int i = 0; i < projectNames.size(); i++) {
            String projectName = projectNames.get(i);
            System.out.println((i + 1) + ". " + projectName + " (" + projectController.checkNeighborhood(projectName) + ")");
        }
        System.out.println("0. Cancel");
    
        System.out.print("Select a project to apply (Enter number) ");
        int choice = getMenuChoice();
        if (choice == 0) {
            System.out.println("Application canceled.");
            return;
        }
        if (choice < 1 || choice > projectNames.size()) {
            System.out.println("Invalid choice. Application canceled.");
            return;
        }
    
        String selectedProjectName = projectNames.get(choice - 1);
        String flatTypeApply;

        if (authController.checkMaritalStatus(userName).equalsIgnoreCase("Married")) {
            boolean hasTwoRoom = projectController.checkTwoRoomUnits(selectedProjectName) > 0;
            boolean hasThreeRoom = projectController.checkThreeRoomUnits(selectedProjectName) > 0;
            
            if (hasTwoRoom && hasThreeRoom) {
                System.out.println("1. 2-Room");
                System.out.println("2. 3-Room");
            } else if (hasTwoRoom) {
                System.out.println("1. 2-Room");
            } else if (hasThreeRoom) {
                System.out.println("1. 3-Room");
            }
            System.out.println("0. Cancel");
        
            System.out.println("Select a flat type to apply (Enter number) ");
            choice = getMenuChoice();
            
            switch (choice) {
                case 0 -> {
                    System.out.println("Application canceled.");
                    return;
                }
                case 1 -> {
                    if (hasTwoRoom) {
                        flatTypeApply = "2-room";
                    } else if (hasThreeRoom) {
                        flatTypeApply = "3-room";
                    } else {
                        System.out.println("Invalid choice. Application canceled.");
                        return;
                    }
                }
                case 2 -> {
                    if (hasTwoRoom && hasThreeRoom) {
                        flatTypeApply = "3-room";
                    } else {
                        System.out.println("Invalid choice. Application canceled.");
                        return;
                    }
                }
                default -> {
                    System.out.println("Invalid choice. Application canceled.");
                    return;
                }
            }
        } else {
            flatTypeApply = "2-room";
        }

        System.out.println("\nYou are about to apply for:");
        System.out.println("Project: " + selectedProjectName);
        System.out.println("Flat Type: " + flatTypeApply);
        System.out.println("\nConfirm application?");
        System.out.println("1. Yes, submit application");
        System.out.println("0. No, cancel");
        
        int confirmChoice = getMenuChoice();
        if (confirmChoice != 1) {
            System.out.println("Application cancelled.");
            return;
        }
    
        if (applicationController.applyForProject(userName, selectedProjectName, flatTypeApply)) {
            System.out.println("Application submitted successfully!");
        } else {
            System.out.println("Failed to apply. You my have an existing application or be ineligible.");
        }
    }

    @Override
    public void viewMyApplication(String userName) {
        if (applicationController.getApplication(userName) == null) {
            System.out.println("You have no active application.");
            return;
        }

        System.out.println("\n=== Your Application ===");
        System.out.println("Project: " + applicationController.checkProjectName(userName));
        System.out.println("Flat Type Apply: " + applicationController.checkFlatTypeApply(userName));
        System.out.println("Status: " + applicationController.checkStatus(userName));
        if ("SUCCESSFUL".equals(applicationController.checkStatus(userName).toString())) {
            String projectName = applicationController.checkProjectName(userName);
            System.out.println("Officers you may contact: " + projectController.checkOfficers(projectName));
        }
        if (applicationController.checkFlatType(userName) != null) {
            System.out.println("Flat Type: " + applicationController.checkFlatType(userName));
            System.out.println("Price: " + applicationController.checkPrice(userName));
        }
    }

    @Override
    public void withdrawApplication(String userName) {
        if (applicationController.getApplication(userName) == null) {
            System.out.println("You have no active application to withdraw.");
            return;
        }
        
        System.out.println("\n=== Withdraw Application ===");
        System.out.println("You are about to withdraw your application for project: " + 
            applicationController.checkProjectName(userName));
        System.out.println("This action cannot be undone. Are you sure?");
        System.out.println("1. Yes, withdraw my application");
        System.out.println("0. No, cancel");
        
        int choice = getMenuChoice();
        
        if (choice != 1) {
            System.out.println("Withdrawal canceled.");
            return;
        }
        
        if (applicationController.requestWithdrawal(userName)) {
            System.out.println("Application withdrawn successfully.");
        } else {
            System.out.println("Failed to withdraw application. It may already be processed or not eligible.");
        }
    }
    @Override
    public void displayEnquiryMenu(String userName) {
        while (true) {
            System.out.println("\n=== Enquiry Menu ===");
            System.out.println("1. Create Enquiry");
            System.out.println("2. View My Enquiries");
            System.out.println("3. Edit Enquiry");
            System.out.println("4. Delete Enquiry");
            System.out.println("0. Back to Applicant Menu");
    
            int choice = getMenuChoice();
            switch (choice) {
                case 1 -> createEnquiry(userName);
                case 2 -> viewMyEnquiries(userName);
                case 3 -> editEnquiry(userName);
                case 4 -> deleteEnquiry(userName);
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public void createEnquiry(String userName) {
        List<String> projectNames = projectController.getVisibleProjects();

        if (projectNames.isEmpty()) {
            System.out.println("No projects available for enquiry.");
            return;
        }

        System.out.println("\n=== Available Projects ===");
        for (int i = 0; i < projectNames.size(); i++) {
            String projectName = projectNames.get(i);
            System.out.println((i + 1) + ". " + projectName + " (" + projectController.checkNeighborhood(projectName) + ")");
        }

        System.out.print("Select a project for enquiry (Enter number) ");
        int choice = getMenuChoice();
        if (choice < 1 || choice > projectNames.size()) {
            System.out.println("Invalid choice. Enquiry creation canceled.");
            return;
        }

        String selectedProjectName = projectNames.get(choice - 1);

        System.out.print("Enter your question: ");
        String question = scanner.nextLine();

        String enquiryId = enquiryController.createEnquiry(userName, selectedProjectName, question).getId();
        System.out.println("Enquiry created with ID: " + enquiryId);
        System.out.println("Posted on: " + enquiryController.checkFormattedCreatedDate(enquiryId));
    }

    @Override
    public void viewMyEnquiries(String userName) {
        List<String> enquiryIds = enquiryController.getEnquiriesByApplicant(userName);

        if (enquiryIds.isEmpty()) {
            System.out.println("You have no enquiries.");
            return;
        }

        System.out.println("\n=== Your Enquiries ===");
        for (int i = 0; i < enquiryIds.size(); i++) {
            displayEnquiry(i, enquiryIds.get(i), false);
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
    public void editEnquiry(String userName) {
        List<String> enquiryIds = enquiryController.getEnquiriesByApplicant(userName);
        
        if (enquiryIds.isEmpty()) {
            System.out.println("You have no enquiries to edit.");
            return;
        }
        
        List<String> editableEnquiryIds = new ArrayList<>();
        for (String enquiryId : enquiryIds) {
            if (enquiryController.getEnquiry(enquiryId) == null || 
                enquiryController.checkAnswer(enquiryId) == null || 
                enquiryController.checkAnswer(enquiryId).isEmpty()) {
                editableEnquiryIds.add(enquiryId);
            }
        }
        
        if (editableEnquiryIds.isEmpty()) {
            System.out.println("You have no enquiries that can be edited. Answered enquiries cannot be modified.");
            return;
        }
        
        System.out.println("\n=== Edit Enquiry ===");
        for (int i = 0; i < editableEnquiryIds.size(); i++) {
            String enquiryId = editableEnquiryIds.get(i);
            System.out.println((i + 1) + ". ID: " + enquiryId + 
                            " | Project: " + enquiryController.checkProjectName(enquiryId) + 
                            " | Question: " + enquiryController.checkQuestion(enquiryId));
        }
        System.out.println("0. Cancel");
        
        System.out.print("Select an enquiry to edit (Enter number) ");
        int choice = getMenuChoice();
        if (choice == 0) {
            System.out.println("Edit canceled.");
            return;
        }
        if (choice < 1 || choice > editableEnquiryIds.size()) {
            System.out.println("Invalid choice. Edit canceled.");
            return;
        }
        
        String selectedEnquiryId = editableEnquiryIds.get(choice - 1);
        
        System.out.print("Enter new question: ");
        String newQuestion = scanner.nextLine();
        
        boolean success = enquiryController.updateEnquiry(selectedEnquiryId, newQuestion);
        System.out.println(success ? "Enquiry updated." : "Failed to update enquiry.");
    }

    @Override
    public void deleteEnquiry(String userName) {
        List<String> enquiryIds = enquiryController.getEnquiriesByApplicant(userName);
        
        if (enquiryIds.isEmpty()) {
            System.out.println("You have no enquiries to delete.");
            return;
        }
        
        List<String> deletableEnquiryIds = new ArrayList<>();
        for (String enquiryId : enquiryIds) {
            if (enquiryController.getEnquiry(enquiryId) == null || 
                enquiryController.checkAnswer(enquiryId) == null ||
                enquiryController.checkAnswer(enquiryId).isEmpty()) {
                deletableEnquiryIds.add(enquiryId);
            }
        }
        
        if (deletableEnquiryIds.isEmpty()) {
            System.out.println("You have no enquiries that can be deleted. Answered enquiries cannot be removed.");
            return;
        }
        
        System.out.println("\n=== Delete Enquiry ===");
        for (int i = 0; i < deletableEnquiryIds.size(); i++) {
            String enquiryId = deletableEnquiryIds.get(i);
            System.out.println((i + 1) + ". ID: " + enquiryId + 
                            " | Project: " + enquiryController.checkProjectName(enquiryId) + 
                            " | Question: " + enquiryController.checkQuestion(enquiryId));
        }
        System.out.println("0. Cancel");
        
        System.out.print("Select an enquiry to delete (Enter number) ");
        int choice = getMenuChoice();
        if (choice == 0) {
            System.out.println("Deletion canceled.");
            return;
        }
        if (choice < 1 || choice > deletableEnquiryIds.size()) {
            System.out.println("Invalid choice. Deletion canceled.");
            return;
        }
        
        String selectedEnquiryId = deletableEnquiryIds.get(choice - 1);
        
        boolean success = enquiryController.deleteEnquiry(selectedEnquiryId);
        System.out.println(success ? "Enquiry deleted." : "Failed to delete enquiry.");
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