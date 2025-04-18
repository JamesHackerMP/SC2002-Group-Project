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

        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.printf("%d. %s (Neighborhood: %s, Slots: %d/%d, Application Period: %s to %s)%n",
                    i + 1, project.getName(), project.getNeighborhood(),
                    project.getOfficers().size(), project.getOfficerSlots(),
                    project.getOpeningDate(), project.getClosingDate());
        }
        System.out.println("0. Cancel");

        System.out.print("\nSelect a project to register (Enter number) ");
        int choice = getMenuChoice();

        if (choice == 0) return;

        if (choice < 1 || choice > projects.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Project selectedProject = projects.get(choice - 1);
        if (officerController.registerForProject(officer, selectedProject.getName())) {

            applicationController.getAllApplications().removeIf(app -> 
            app.getApplicantName().equals(officer.getName()) && 
            app.getStatus() == Application.Status.UNSUCCESSFUL);

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
            System.out.println("\n=== Assigned Projects ===");
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
        
            List<Project> allProjects = filterController.applyFilters(assignedProjects);
        
            if (allProjects.isEmpty()) {
                System.out.println("No projects available with the current filters.");
                return;
            }
            
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
        Project currentProject = projectController.getCurrentProjectByOfficer(officer.getName());
        if (currentProject == null) {
            System.out.println("You are not assigned to any project that opens for applying.");
            return;
        }

        List<Application> applications = applicationController.getAllApplications();
        List<Application> projectApplications = new ArrayList<>();

        for (Application application : applications) {
            if (application.getProjectName().equals(currentProject.getName()) &&
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

        System.out.print("\nSelect an application to process (Enter number) ");
        int choice = getMenuChoice();

        if (choice == 0) return;

        if (choice < 1 || choice > projectApplications.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        Application selectedApplication = projectApplications.get(choice - 1);
        User applicant = authController.getUser(selectedApplication.getApplicantName());
        String flatType;

        System.out.println("\nAvailable Flat Types:");
        if (applicant.getMaritalStatus().equals("Single")) {
            System.out.println("1. 2-Room");
            System.out.println("0. Cancel");
            
            System.out.print("Select flat type to book (Enter number) ");
            int flatChoice = getMenuChoice();
            
            switch (flatChoice) {
                case 0:
                    System.out.println("Booking canceled.");
                    return;
                case 1:
                    flatType = "2-Room";
                    break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }
        } else {
            boolean hasTwoRoom = currentProject.getTwoRoomUnits() > 0;
            boolean hasThreeRoom = currentProject.getThreeRoomUnits() > 0;
            
            if (hasTwoRoom && hasThreeRoom) {
                System.out.println("1. 2-Room");
                System.out.println("2. 3-Room");
            } else if (hasTwoRoom) {
                System.out.println("1. 2-Room");
            } else if (hasThreeRoom) {
                System.out.println("1. 3-Room");
            }
            System.out.println("0. Cancel");
            
            System.out.print("Select flat type to book (Enter number) ");
            int flatChoice = getMenuChoice();
            
            switch (flatChoice) {
                case 0:
                    System.out.println("Booking canceled.");
                    return;
                case 1:
                    if (hasTwoRoom) {
                        flatType = "2-Room";
                    } else if (hasThreeRoom) {
                        flatType = "3-Room";
                    } else {
                        System.out.println("Invalid choice.");
                        return;
                    }
                    break;
                case 2:
                    if (hasTwoRoom && hasThreeRoom) {
                        flatType = "3-Room";
                    } else {
                        System.out.println("Invalid choice.");
                        return;
                    }
                    break;
                default:
                    System.out.println("Invalid choice.");
                    return;
            }
        }

        if (applicationController.bookFlat(selectedApplication.getApplicantName(), flatType, officer.getName())) {
            System.out.println("Flat booked successfully!");
        } else {
            System.out.println("Failed to book flat. Please check availability.");
        }
    }

    @Override
    public void generateBookingReceipt(HDBOfficer officer) {
        List<Project> assignedProjects = projectController.getProjectsByOfficer(officer.getName());
    
        if ((assignedProjects == null || assignedProjects.isEmpty())) {
            System.out.println("You are not assigned to any project.");
            return;
        }

        List<Application> applications = applicationController.getAllApplications();
        List<Application> bookedApplications = new ArrayList<>();
        
        if (assignedProjects != null && !assignedProjects.isEmpty()) {
            for (Application application : applications) {
                for (Project project : assignedProjects) {
                    if (application.getProjectName().equals(project.getName()) && 
                        application.getStatus() == Application.Status.BOOKED) {
                        bookedApplications.add(application);
                        break;
                    }
                }
            }
        }
        
        if (bookedApplications.isEmpty()) {
            System.out.println("No completed bookings found for your assigned projects.");
            return;
        }

        System.out.println("\n=== Completed Bookings ===");
        for (int i = 0; i < bookedApplications.size(); i++) {
            Application application = bookedApplications.get(i);
            System.out.printf("%d. %s%n", i + 1, application.getApplicantName());
        }
        System.out.println("0. Cancel");
    
        System.out.print("\nSelect a booking to generate receipt (Enter number) ");
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
        Project currentProject = projectController.getCurrentProjectByOfficer(officer.getName());
        if (currentProject == null) {
            System.out.println("You are not assigned to any project that opens for applying that opens for applying.");
            return;
        }

        List<Enquiry> enquiries = enquiryController.getEnquiriesByProject(currentProject.getName());

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

        int choice;
        while (true) {
            System.out.print("\nSelect an enquiry to reply (Enter number) ");
            choice = getMenuChoice();
            
            if (choice == 0) return;
            
            if (choice < 0 || choice > enquiries.size()) {
                System.out.println("Invalid selection. Please try again.");
            } else if (choice >= 1 && choice <= enquiries.size()) {
                break;
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