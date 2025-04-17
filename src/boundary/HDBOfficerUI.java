package boundary;

import boundary.interfaces.officer.*;
import control.*;
import entity.*;
import java.util.*;

public class HDBOfficerUI implements ProjectManagementUI, BookingManagementUI, 
                                    EnquiryManagementUI, MenuDisplayUI {
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiryController enquiryController;
    private final OfficerController officerController;
    private final AuthenticationController authController;     
    private final FilterController filterController;
    private final Scanner scanner;

    public HDBOfficerUI(ProjectController projectController,
                    ApplicationController applicationController,
                    EnquiryController enquiryController,
                    OfficerController officerController,
                    AuthenticationController authController,
                    FilterController filterController) {
        this.projectController = projectController;
        this.applicationController = applicationController;
        this.enquiryController = enquiryController;
        this.officerController = officerController;
        this.authController = authController;
        this.filterController = filterController;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void displayMenu(HDBOfficer officer) {
        while (true) {
            System.out.println("\n=== HDB Officer Menu ===");
            System.out.println("1. Register for Project Team");
            System.out.println("2. View My Project");
            System.out.println("3. Process Flat Booking");
            System.out.println("4. Generate Booking Receipt");
            System.out.println("5. Manage Enquiries");
            System.out.println("0. Back to Main Menu");

            int choice = getMenuChoice();
            switch (choice) {
                case 1 -> registerForProject(officer);
                case 2 -> viewMyProject(officer);
                case 3 -> processFlatBooking(officer);
                case 4 -> generateBookingReceipt(officer);
                case 5 -> manageEnquiries(officer);
                case 0 -> {return;}
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public void registerForProject(HDBOfficer officer) {
        System.out.println("\n=== Available Projects for Registration ===");
        List<Project> projects = projectController.getAllProjects();

        if (projects.isEmpty()) {
            System.out.println("No projects available for registration.");
            return;
        }

        projects.removeIf(project -> applicationController.getAllApplications().stream()
        .anyMatch(application -> 
            application.getApplicantName().equals(officer.getName()) &&
            application.getProjectName().equals(project.getName())
        ));

        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.printf("%d. %s (Neighborhood: %s, Slots: %d/%d, Application Period: %s to %s)%n",
                    i + 1, project.getName(), project.getNeighborhood(),
                    project.getOfficers().size(), project.getOfficerSlots(),
                    project.getOpeningDate(), project.getClosingDate());
        }
        System.out.println("0. Cancel");

        System.out.print("\nSelect a project to register (Enter number): ");
        int choice = getMenuChoice();

        if (choice == 0) return;

        if (choice < 1 || choice > projects.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Project selectedProject = projects.get(choice - 1);
        if (officerController.registerForProject(officer, selectedProject.getName())) {
            System.out.println("Registration submitted for approval.");
        } else {
            System.out.println("Registration failed. You may be ineligible.");
        }
    }

    @Override
    public void viewMyProject(HDBOfficer officer) {
        List<Project> assignedProjects = projectController.getProjectsByOfficer(officer.getName());
    
        List<Project> pendingProjects = projectController.getAllProjects().stream()
                .filter(project -> project.getPendingOfficers().contains(officer.getName()))
                .toList();
    
        if ((assignedProjects == null || assignedProjects.isEmpty()) && pendingProjects.isEmpty()) {
            System.out.println("You are not assigned to any project, and you have no pending registrations.");
            return;
        }
    
        if (assignedProjects != null && !assignedProjects.isEmpty()) {
            System.out.println("\nWould you like to apply filters to your projects? (yes/no): ");
            String applyFilters = scanner.nextLine().trim().toLowerCase();

            if (applyFilters.equals("yes")) {
                assignedProjects = filterController.applyFilters(assignedProjects);
            }

            System.out.println("\n=== Assigned Projects ===");
            for (Project project : assignedProjects) {
                System.out.println("\nProject Name: " + project.getName());
                System.out.println("Neighborhood: " + project.getNeighborhood());
                System.out.println("2-Room Units Available: " + project.getTwoRoomUnits());
                System.out.println("2-Room Price: " + project.getTwoRoomPrice());
                System.out.println("3-Room Units Available: " + project.getThreeRoomUnits());
                System.out.println("3-Room Price: " + project.getThreeRoomPrice());
                System.out.println("Application Period: " + project.getOpeningDate() + " to " + project.getClosingDate());
                System.out.println("Manager: " + project.getManager());
            }
        }
    
        if (!pendingProjects.isEmpty()) {
            System.out.println("\n=== Pending Registrations ===");
            for (Project project : pendingProjects) {
                System.out.println("\nProject Name: " + project.getName());
                System.out.println("Neighborhood: " + project.getNeighborhood());
                System.out.println("Application Period: " + project.getOpeningDate() + " to " + project.getClosingDate());
                System.out.println("Manager: " + project.getManager());
                System.out.println("Status: Pending Approval");
            }
        }
    }

    @Override
    public void processFlatBooking(HDBOfficer officer) {
        List<Project> assignedProjects = projectController.getProjectsByOfficer(officer.getName());
        if (assignedProjects == null || assignedProjects.isEmpty()) {
            System.out.println("You are not assigned to any project.");
            return;
        }

        List<Application> applications = applicationController.getAllApplications();
        List<Application> projectApplications = new ArrayList<>();

        for (Application application : applications) {
            if (application.getProjectName().equals(projectController.getCurrentProjectByOfficer(officer.getName()).getName()) &&
                application.getStatus() == Application.Status.SUCCESSFUL) {
                projectApplications.add(application);
            }
        }

        if (projectApplications.isEmpty()) {
            System.out.println("No successful applications found for this project.");
            return;
        }

        System.out.println("\n=== Successful Application ===");
        for (int i = 0; i < projectApplications.size(); i++) {
            Application application = projectApplications.get(i);
            System.out.printf("%d. %s%n", i + 1, application.getApplicantName());
            System.out.println("NRIC: " + authController.getUser(application.getApplicantName()).getNric());
            System.out.println("Flat Type Apply: " + application.getFlatTypeApply());
        }
        System.out.println("0. Cancel");

        System.out.print("\nSelect an application to process (Enter number): ");
        int choice = getMenuChoice();

        if (choice == 0) return;

        if (choice < 1 || choice > projectApplications.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Application selectedApplication = projectApplications.get(choice - 1);
        User applicant = authController.getUser(selectedApplication.getApplicantName());
        Project project = projectController.getCurrentProjectByOfficer(officer.getName());

        System.out.println("\nAvailable Flat Types:");
        if (project.getTwoRoomUnits() > 0) {
            System.out.println("1. 2-Room");
        }
        if (project.getThreeRoomUnits() > 0 & applicant.getMaritalStatus().equalsIgnoreCase("Married")) {
            System.out.println("2. 3-Room");
        }
        System.out.println("0. Cancel");
    
        System.out.print("Select flat type to book (Enter number): ");
        int flatChoice = getMenuChoice();

        String flatType = switch (flatChoice) {
            case 1 -> "2-Room";
            case 2 -> "3-Room";
            default -> null;
        };

        if (flatType == null) {
            System.out.println("Invalid choice.");
            return;
        }

        if (applicationController.bookFlat(selectedApplication.getApplicantName(), flatType, officer.getName())) {
            System.out.println("Flat booked successfully!");
        } else {
            System.out.println("Failed to book flat. Please check availability.");
        }
    }

    @Override
    public void generateBookingReceipt(HDBOfficer officer) {
        if (projectController.getCurrentProjectByOfficer(officer.getName()) == null) {
            System.out.println("You must be assigned to a project to generate receipts.");
            return;
        }

        List<Application> applications = applicationController.getAllApplications();
        List<Application> bookedApplications = new ArrayList<>();

        for (Application application : applications) {
            if (application.getProjectName().equals(projectController.getCurrentProjectByOfficer(officer.getName()).getName()) &&
                application.getStatus() == Application.Status.BOOKED) {
                bookedApplications.add(application);
            }
        }

        if (bookedApplications.isEmpty()) {
            System.out.println("No completed bookings found for this project.");
            return;
        }

        System.out.println("\n=== Completed Bookings ===");
        for (int i = 0; i < bookedApplications.size(); i++) {
            Application application = bookedApplications.get(i);
            System.out.printf("%d. %s%n", i + 1, application.getApplicantName());
        }
        System.out.println("0. Cancel");
    
        System.out.print("\nSelect a booking to generate receipt (Enter number): ");
        int choice = getMenuChoice();

        if (choice == 0) return;

        if (choice < 1 || choice > bookedApplications.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Application selectedApplication = bookedApplications.get(choice - 1);
        Project project = projectController.getProject(selectedApplication.getProjectName());
        User applicant = authController.getUser(selectedApplication.getApplicantName());
        System.out.println("\n=== Booking Receipt ===");
        System.out.println("Applicant Name: " + applicant.getName());
        System.out.println("Nric: " + applicant.getNric());
        System.out.println("Age: " + applicant.getAge());
        System.out.println("Marital Status: " + applicant.getMaritalStatus());
        System.out.println("Project: " + project.getName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Flat Type: " + selectedApplication.getFlatType());
        System.out.println("Price: " + selectedApplication.getPrice());
        System.out.println("Booking Date: " + java.time.LocalDate.now());
        System.out.println("\nThank you for choosing HDB!");
    }

    @Override
    public void manageEnquiries(HDBOfficer officer) {
        if (projectController.getCurrentProjectByOfficer(officer.getName()) == null) {
            System.out.println("You must be assigned to a project to manage enquiries.");
            return;
        }

        List<Enquiry> enquiries = enquiryController.getEnquiriesByProject(projectController.getCurrentProjectByOfficer(officer.getName()).getName());

        if (enquiries.isEmpty()) {
            System.out.println("No enquiries found for this project.");
            return;
        }

        System.out.println("\n=== Enquiries for " + projectController.getCurrentProjectByOfficer(officer.getName()) + " ===");
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". ID: " + enquiry.getId());
            System.out.println("   Project: " + enquiry.getProjectName());
            System.out.println("   Applicant: " + enquiry.getApplicantName());
            System.out.println("   Question: " + enquiry.getQuestion());
            System.out.println("   Posted: " + enquiry.getFormattedCreatedDate());
            if (enquiry.getAnswer() != null) {
                System.out.println("   Answer: " + enquiry.getAnswer());
                System.out.println("   Answered: " + enquiry.getFormattedAnsweredDate());
            } else {
                System.out.println("   Status: Pending response");
            }
        }
        System.out.println("0. Cancel");

        int choice = -1;
        while (choice < 0 || choice > enquiries.size()) {
            System.out.print("\nSelect an enquiry to reply (Enter number): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice == 0) return;
                if (choice < 1 || choice > enquiries.size()) {
                    System.out.println("Invalid selection. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }
        }
    
        Enquiry selectedEnquiry = enquiries.get(choice - 1);
        System.out.println("\nQuestion: " + selectedEnquiry.getQuestion());
        System.out.print("Enter your reply: ");
        String answer = scanner.nextLine();
    
        if (enquiryController.replyToEnquiry(selectedEnquiry.getId(), answer)) {
            System.out.println("Reply submitted successfully.");
        } else {
            System.out.println("Failed to submit reply.");
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