package boundary;

import boundary.interfaces.applicant.*;
import control.*;
import entity.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ApplicantUI implements ProjectViewUI, ApplicationManagementUI, 
                                    EnquiryApplicantUI, ApplicantMenuUI {
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiryController enquiryController;
    private final FilterController filterController;
    private final Scanner scanner;

    public ApplicantUI(ProjectController projectController,
                       ApplicationController applicationController,
                       EnquiryController enquiryController, FilterController filterController) {
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiryController = enquiryController;
        this.filterController = filterController;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu(User user) {
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
                case 1 -> viewAvailableProjects(user);
                case 2 -> applyForProject(user);
                case 3 -> viewMyApplication(user);
                case 4 -> withdrawApplication(user);
                case 5 -> displayEnquiryMenu(user);
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public void viewAvailableProjects(User user) {
        System.out.println("\n=== Available Projects ===");
    
        List<Project> projects = filterController.applyFilters(projectController.getVisibleProjects());
    
        projects.removeIf(project -> !applicationController.isEligibleForProject(user, project));

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
    
        if (projects.isEmpty()) {
            System.out.println("No projects available with the current filters.");
            return;
        }
    
        for (Project project : projects) {
            System.out.println("\nProject Name: " + project.getName());
            System.out.println("Neighborhood: " + project.getNeighborhood());
            if (project.getTwoRoomUnits() > 0) {
                System.out.println("2-Room Units Available: " + project.getTwoRoomUnits());
                System.out.println("2-Room Price: " + project.getTwoRoomPrice());
            }
            if (project.getThreeRoomUnits() > 0 && user.getMaritalStatus().equalsIgnoreCase("Married")) {
                System.out.println("3-Room Units Available: " + project.getThreeRoomUnits());
                System.out.println("3-Room Price: " + project.getThreeRoomPrice());
            }
            System.out.println("Application Period: " + project.getOpeningDate() + " to " + project.getClosingDate());
            System.out.println("Manager: " + project.getManager());
            System.out.println("Officers: " + project.getOfficers());
        }
    }

    @Override
    public void applyForProject(User user) {
        List<Project> projects = projectController.getVisibleProjects();
    
        projects.removeIf(project -> !applicationController.isEligibleForProject(user, project));
    
        if (projects.isEmpty()) {
            System.out.println("No projects available for application.");
            return;
        }
    
        System.out.println("\n=== Available Projects ===");
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.println((i + 1) + ". " + project.getName() + " (" + project.getNeighborhood() + ")");
        }
        System.out.println("0. Cancel");
    
        System.out.print("Select a project to apply (Enter number) ");
        int choice = getMenuChoice();
        if (choice == 0) {
            System.out.println("Application canceled.");
            return;
        }
        if (choice < 1 || choice > projects.size()) {
            System.out.println("Invalid choice. Application canceled.");
            return;
        }
    
        Project selectedProject = projects.get(choice - 1);
        String flatTypeApply;

        if (user.getMaritalStatus().equalsIgnoreCase("Married")) {
            boolean hasTwoRoom = selectedProject.getTwoRoomUnits() > 0;
            boolean hasThreeRoom = selectedProject.getThreeRoomUnits() > 0;
            
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
                case 0:
                    System.out.println("Application canceled.");
                    return;
                case 1:
                    if (hasTwoRoom) {
                        flatTypeApply = "2-room";
                    } else if (hasThreeRoom) {
                        flatTypeApply = "3-room";
                    } else {
                        System.out.println("Invalid choice. Application canceled.");
                        return;
                    }
                    break;
                case 2:
                    if (hasTwoRoom && hasThreeRoom) {
                        flatTypeApply = "3-room";
                    } else {
                        System.out.println("Invalid choice. Application canceled.");
                        return;
                    }
                    break;
                default:
                    System.out.println("Invalid choice. Application canceled.");
                    return;
            }
        } else {
            flatTypeApply = "2-room";
        }

        if (applicationController.applyForProject(user, selectedProject.getName(), flatTypeApply)) {
            System.out.println("Application submitted successfully!");
        } else {
            System.out.println("Failed to apply. You my have an existing application or be ineligible.");
        }
    }

    @Override
    public void viewMyApplication(User user) {
        Application application = applicationController.getApplication(user.getName());
        if (application == null) {
            System.out.println("You have no active application.");
            return;
        }

        System.out.println("\n=== Your Application ===");
        System.out.println("Project: " + application.getProjectName());
        System.out.println("Flat Type Apply: " + application.getFlatTypeApply());
        System.out.println("Status: " + application.getStatus());
        if ("SUCCESSFUL".equals(application.getStatus().toString())) {
            Project project = projectController.getProject(application.getProjectName());
            System.out.println("Officers you may contact: " + project.getOfficers());
        }
        if (application.getFlatType() != null) {
            System.out.println("Flat Type: " + application.getFlatType());
            System.out.println("Price: " + application.getPrice());
        }
    }

    @Override
    public void withdrawApplication(User user) {
        Application application = applicationController.getApplication(user.getName());
        
        if (application == null) {
            System.out.println("You have no active application to withdraw.");
            return;
        }
        
        System.out.println("\n=== Withdraw Application ===");
        System.out.println("You are about to withdraw your application for project: " + application.getProjectName());
        System.out.println("This action cannot be undone. Are you sure?");
        System.out.println("1. Yes, withdraw my application");
        System.out.println("0. No, cancel");
        
        int choice = getMenuChoice();
        
        if (choice != 1) {
            System.out.println("Withdrawal canceled.");
            return;
        }
        
        if (applicationController.requestWithdrawal(user.getName())) {
            System.out.println("Application withdrawn successfully.");
        } else {
            System.out.println("Failed to withdraw application. It may already be processed or not eligible.");
        }
    }

    @Override
    public void displayEnquiryMenu(User user) {
        while (true) {
            System.out.println("\n=== Enquiry Menu ===");
            System.out.println("1. Create Enquiry");
            System.out.println("2. View My Enquiries");
            System.out.println("3. Edit Enquiry");
            System.out.println("4. Delete Enquiry");
            System.out.println("0. Back to Applicant Menu");
    
            int choice = getMenuChoice();
            switch (choice) {
                case 1 -> createEnquiry(user);
                case 2 -> viewMyEnquiries(user);
                case 3 -> editEnquiry(user);
                case 4 -> deleteEnquiry(user);
                case 0 -> { return; }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public void createEnquiry(User user) {
        List<Project> projects = projectController.getVisibleProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects available for enquiry.");
            return;
        }

        System.out.println("\n=== Available Projects ===");
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.println((i + 1) + ". " + project.getName() + " (" + project.getNeighborhood() + ")");
        }

        System.out.print("Select a project for enquiry (Enter number) ");
        int choice = getMenuChoice();
        if (choice < 1 || choice > projects.size()) {
            System.out.println("Invalid choice. Enquiry creation canceled.");
            return;
        }

        Project selectedProject = projects.get(choice - 1);

        System.out.print("Enter your question: ");
        String question = scanner.nextLine();

        Enquiry enquiry = enquiryController.createEnquiry(user.getName(), selectedProject.getName(), question);
        System.out.println("Enquiry created with ID: " + enquiry.getId());
    }

    @Override
    public void viewMyEnquiries(User user) {
        List<Enquiry> enquiries = enquiryController.getEnquiriesByApplicant(user.getName());

        if (enquiries.isEmpty()) {
            System.out.println("You have no enquiries.");
            return;
        }

        System.out.println("\n=== Your Enquiries ===");
        for (Enquiry enquiry : enquiries) {
            System.out.println("\nID: " + enquiry.getId());
            System.out.println("Project: " + enquiry.getProjectName());
            System.out.println("Question: " + enquiry.getQuestion());
            System.out.println("Status: " + (enquiry.getAnswer() != null ? "Answered" : "Pending"));
            if (enquiry.getAnswer() != null && !enquiry.getAnswer().isEmpty()) {
                System.out.println("Answer: " + enquiry.getAnswer());
            } else {
                System.out.println("No answer yet.");
            }
        }
    }

    @Override
    public void editEnquiry(User user) {
        List<Enquiry> enquiries = enquiryController.getEnquiriesByApplicant(user.getName());
        
        if (enquiries.isEmpty()) {
            System.out.println("You have no enquiries to edit.");
            return;
        }
        
        List<Enquiry> editableEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getAnswer() == null || enquiry.getAnswer().isEmpty()) {
                editableEnquiries.add(enquiry);
            }
        }
        
        if (editableEnquiries.isEmpty()) {
            System.out.println("You have no enquiries that can be edited. Answered enquiries cannot be modified.");
            return;
        }
        
        System.out.println("\n=== Edit Enquiry ===");
        for (int i = 0; i < editableEnquiries.size(); i++) {
            Enquiry enquiry = editableEnquiries.get(i);
            System.out.println((i + 1) + ". ID: " + enquiry.getId() + " | Project: " + enquiry.getProjectName() + " | Question: " + enquiry.getQuestion());
        }
        System.out.println("0. Cancel");
        
        System.out.print("Select an enquiry to edit (Enter number) ");
        int choice = getMenuChoice();
        if (choice == 0) {
            System.out.println("Edit canceled.");
            return;
        }
        if (choice < 1 || choice > editableEnquiries.size()) {
            System.out.println("Invalid choice. Edit canceled.");
            return;
        }
        
        Enquiry selectedEnquiry = editableEnquiries.get(choice - 1);
        
        System.out.print("Enter new question: ");
        String newQuestion = scanner.nextLine();
        
        boolean success = enquiryController.updateEnquiry(selectedEnquiry.getId(), newQuestion);
        System.out.println(success ? "Enquiry updated." : "Failed to update enquiry.");
    }

    @Override
    public void deleteEnquiry(User user) {
        List<Enquiry> enquiries = enquiryController.getEnquiriesByApplicant(user.getName());
        
        if (enquiries.isEmpty()) {
            System.out.println("You have no enquiries to delete.");
            return;
        }
        
        List<Enquiry> deletableEnquiries = new ArrayList<>();
        for (Enquiry enquiry : enquiries) {
            if (enquiry.getAnswer() == null || enquiry.getAnswer().isEmpty()) {
                deletableEnquiries.add(enquiry);
            }
        }
        
        if (deletableEnquiries.isEmpty()) {
            System.out.println("You have no enquiries that can be deleted. Answered enquiries cannot be removed.");
            return;
        }
        
        System.out.println("\n=== Delete Enquiry ===");
        for (int i = 0; i < deletableEnquiries.size(); i++) {
            Enquiry enquiry = deletableEnquiries.get(i);
            System.out.println((i + 1) + ". ID: " + enquiry.getId() + " | Project: " + enquiry.getProjectName() + " | Question: " + enquiry.getQuestion());
        }
        System.out.println("0. Cancel");
        
        System.out.print("Select an enquiry to delete (Enter number) ");
        int choice = getMenuChoice();
        if (choice == 0) {
            System.out.println("Deletion canceled.");
            return;
        }
        if (choice < 1 || choice > deletableEnquiries.size()) {
            System.out.println("Invalid choice. Deletion canceled.");
            return;
        }
        
        Enquiry selectedEnquiry = deletableEnquiries.get(choice - 1);
        
        boolean success = enquiryController.deleteEnquiry(selectedEnquiry.getId());
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