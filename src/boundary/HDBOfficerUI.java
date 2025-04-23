package boundary;

import boundary.interfaces.officer.*;
import control.*;
import java.time.LocalDate;
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
    public void displayMenu(String officerName) {
        while (true) {
            System.out.println("\n=== HDB Officer Menu ===");
            System.out.println("1. Register for Project");
            System.out.println("2. View My Project");
            System.out.println("3. Process Flat Booking");
            System.out.println("4. Generate Booking Receipt");
            System.out.println("5. Manage Enquiries");
            System.out.println("0. Back to Main Menu");

            int choice = getMenuChoice();
            switch (choice) {
                case 1 -> registerForProject(officerName);
                case 2 -> viewMyProject(officerName);
                case 3 -> processFlatBooking(officerName);
                case 4 -> generateBookingReceipt(officerName);
                case 5 -> manageEnquiries(officerName);
                case 0 -> {return;}
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    @Override
    public void registerForProject(String officerName) {
        System.out.println("\n=== Available Projects for Registration ===");
        List<String> projectNames = projectController.getAllProjects();

        if (projectNames.isEmpty()) {
            System.out.println("No projects available for registration.");
            return;
        }

        for (int i = 0; i < projectNames.size(); i++) {
            String projectName = projectNames.get(i);
            System.out.printf("%d. %s (Neighborhood: %s, Slots: %d/%d, Application Period: %s to %s)%n",
                    i + 1, projectName, projectController.checkNeighborhood(projectName),
                    projectController.checkOfficers(projectName).size(), projectController.checkOfficerSlots(projectName),
                    projectController.checkOpeningDate(projectName), projectController.checkClosingDate(projectName));
        }
        System.out.println("0. Cancel");

        System.out.print("\nSelect a project to register (Enter number) ");
        int choice = getMenuChoice();

        if (choice == 0) return;

        if (choice < 1 || choice > projectNames.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        String selectedProjectName = projectNames.get(choice - 1);
        if (officerController.registerForProject(authController.getOfficer(officerName), selectedProjectName)) {

            applicationController.getAllApplications().removeIf(app -> 
            app.equals(officerName) && 
            applicationController.isStatusUnsuccessful(app));
            
            System.out.println("Registration submitted for approval.");
        } else {
            System.out.println("Registration failed. You may be ineligible.");
        }
    }

    @Override
    public void viewMyProject(String officerName) {
        List<String> assignedProjectNames = projectController.getProjectsByOfficer(officerName);
        List<String> pendingProjectNames = projectController.getAllProjects().stream()
                .filter(project -> projectController.checkPendingOfficers(project).contains(officerName))
                .toList();
    
        if ((assignedProjectNames == null || assignedProjectNames.isEmpty()) && pendingProjectNames.isEmpty()) {
            System.out.println("You are not assigned to any project, and you have no pending registrations.");
            return;
        }
    
        if (assignedProjectNames != null && !assignedProjectNames.isEmpty()) {
            System.out.println("\n=== Assigned Projects ===");
            
            System.out.println("Current Filters:");
            String neighborhood = filterController.checkNeighborhood();
            if (neighborhood != null) {
                System.out.println("Neighborhood: " + neighborhood);
            }
            
            List<String> flatTypes = filterController.checkFlatTypes();
            if (flatTypes != null && !flatTypes.isEmpty()) {
                System.out.println("Flat Types: " + flatTypes);
            }
            
            LocalDate openingAfter = filterController.checkOpeningAfter();
            if (openingAfter != null) {
                System.out.println("Opening After: " + openingAfter);
            }
            
            LocalDate closingBefore = filterController.checkClosingBefore();
            if (closingBefore != null) {
                System.out.println("Closing Before: " + closingBefore);
            }
            
            String manager = filterController.checkManager();
            if (manager != null) {
                System.out.println("Manager: " + manager);
            }
            
            String filterOfficer = filterController.checkOfficer();
            if (filterOfficer != null) {
                System.out.println("Officer: " + filterOfficer);
            }
        
            List<String> filteredProjects = filterController.applyFilters(assignedProjectNames);
        
            if (filteredProjects.isEmpty()) {
                System.out.println("No projects available with the current filters.");
                return;
            }
            
            for (String projectName : assignedProjectNames) {
                System.out.println("\nProject Name: " + projectName);
                System.out.println("Neighborhood: " + projectController.checkNeighborhood(projectName));
                System.out.println("2-Room Units: " + projectController.checkTwoRoomUnits(projectName));
                System.out.println("2-Room Price: " + projectController.checkTwoRoomPrice(projectName));
                System.out.println("3-Room Units: " + projectController.checkThreeRoomUnits(projectName));
                System.out.println("3-Room Price: " + projectController.checkThreeRoomPrice(projectName));
                System.out.println("Application Period: " + projectController.checkOpeningDate(projectName) + 
                                " to " + projectController.checkClosingDate(projectName));
                System.out.println("Manager: " + projectController.checkManager(projectName));
            }
        }
    
        if (!pendingProjectNames.isEmpty()) {
            System.out.println("\n=== Pending Registrations ===");
            for (String projectName : pendingProjectNames) {
                System.out.println("\nProject Name: " + projectName);
                System.out.println("Neighborhood: " + projectController.checkNeighborhood(projectName));
                System.out.println("Application Period: " + projectController.checkOpeningDate(projectName) + 
                                " to " + projectController.checkClosingDate(projectName));
                System.out.println("Manager: " + projectController.checkManager(projectName));
                System.out.println("Status: Pending Approval");
            }
        }
    }

    @Override
    public void processFlatBooking(String officerName) {
        String currentProjectName = projectController.getCurrentProjectByOfficer(officerName);
        if (currentProjectName == null) {
            System.out.println("You are not assigned to any project that opens for applying.");
            return;
        }

        List<String> applications = applicationController.getAllApplications();
        List<String> applicantNames = new ArrayList<>();

        for (String application : applications) {
            if (applicationController.checkProjectName(application).equals(currentProjectName) &&
                applicationController.isStatusSuccessful(application)) {
                    applicantNames.add(application);
            }
        }

        if (applicantNames.isEmpty()) {
            System.out.println("No successful applications found for this project.");
            return;
        }

        System.out.println("\n=== Successful Application ===");
        for (int i = 0; i < applicantNames.size(); i++) {
            String applicantName = applicantNames.get(i);
            System.out.printf("%d. %s%n", i + 1, applicantName);
            System.out.println("NRIC: " + authController.checkNric(applicantName));
            System.out.println("Flat Type Apply: " + applicationController.checkFlatTypeApply(applicantName));
        }
        System.out.println("0. Cancel");

        System.out.print("\nSelect an application to process (Enter number) ");
        int choice = getMenuChoice();

        if (choice == 0) return;

        if (choice < 1 || choice > applicantNames.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        String selectedApplicantName = applicantNames.get(choice - 1);
        String maritalStatus = authController.checkMaritalStatus(selectedApplicantName);
        String flatType;

        System.out.println("\nAvailable Flat Types:");
        if ("Single".equals(maritalStatus)) {
            System.out.println("1. 2-Room");
            System.out.println("0. Cancel");
            
            System.out.print("Select flat type to book (Enter number) ");
            int flatChoice = getMenuChoice();
            
            switch (flatChoice) {
                case 0 -> {
                    System.out.println("Booking canceled.");
                    return;
                }
                case 1 -> flatType = "2-Room";
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }
        } else {
            boolean hasTwoRoom = projectController.checkTwoRoomUnits(currentProjectName) > 0;
            boolean hasThreeRoom = projectController.checkThreeRoomUnits(currentProjectName) > 0;
            
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
                case 0 -> {
                    System.out.println("Booking canceled.");
                    return;
                }
                case 1 -> {
                    if (hasTwoRoom) {
                        flatType = "2-Room";
                    } else if (hasThreeRoom) {
                        flatType = "3-Room";
                    } else {
                        System.out.println("Invalid choice.");
                        return;
                    }
                }
                case 2 -> {
                    if (hasTwoRoom && hasThreeRoom) {
                        flatType = "3-Room";
                    } else {
                        System.out.println("Invalid choice.");
                        return;
                    }
                }
                default -> {
                    System.out.println("Invalid choice.");
                    return;
                }
            }
        }

        System.out.println("\nAre you sure you want to book a " + flatType + " flat for " 
        + selectedApplicantName + "?");
        System.out.println("This action finalizes the booking.");
        System.out.println("1. Yes, confirm booking");
        System.out.println("0. No, cancel");

        int confirmChoice = getMenuChoice();
        if (confirmChoice != 1) {
            System.out.println("Booking cancelled.");
            return;
        }

        if (applicationController.bookFlat(selectedApplicantName, flatType, officerName)) {
            System.out.println("Flat booked successfully!");
        } else {
            System.out.println("Failed to book flat. Please check availability.");
        }
    }

    @Override
    public void generateBookingReceipt(String officerName) {
        List<String> assignedProjectNames = projectController.getProjectsByOfficer(officerName);

        if (assignedProjectNames == null || assignedProjectNames.isEmpty()) {
            System.out.println("You are not assigned to any project.");
            return;
        }

        List<String> applications = applicationController.getAllApplications();
        List<String> bookedApplicantNames = new ArrayList<>();
        
        if (!assignedProjectNames.isEmpty()) {
            for (String application : applications) {
                for (String projectName : assignedProjectNames) {
                    if (applicationController.checkProjectName(application).equals(projectName) && 
                        applicationController.isStatusBooked(application)) {
                            bookedApplicantNames.add(application);
                        break;
                    }
                }
            }
        }
        
        if (bookedApplicantNames.isEmpty()) {
            System.out.println("No completed bookings found for your assigned projects.");
            return;
        }

        System.out.println("\n=== Completed Bookings ===");
        for (int i = 0; i < bookedApplicantNames.size(); i++) {
            String applicantName = bookedApplicantNames.get(i);
            System.out.printf("%d. %s%n", i + 1, applicantName);
        }
        System.out.println("0. Cancel");

        System.out.print("\nSelect a booking to generate receipt (Enter number) ");
        int choice = getMenuChoice();

        if (choice == 0) return;

        if (choice < 1 || choice > bookedApplicantNames.size()) {
            System.out.println("Invalid choice.");
            return;
        }

        String selectedApplicantName = bookedApplicantNames.get(choice - 1);
        String projectName = applicationController.checkProjectName(selectedApplicantName);
        
        System.out.println("\n=== Booking Receipt ===");
        System.out.println("Applicant Name: " + selectedApplicantName);
        System.out.println("Nric: " + authController.checkNric(selectedApplicantName));
        System.out.println("Age: " + authController.checkAge(selectedApplicantName));
        System.out.println("Marital Status: " + authController.checkMaritalStatus(selectedApplicantName));
        System.out.println("Project: " + projectName);
        System.out.println("Neighborhood: " + projectController.checkNeighborhood(projectName));
        System.out.println("Flat Type: " + applicationController.checkFlatType(selectedApplicantName));
        System.out.println("Price: " + applicationController.checkPrice(selectedApplicantName));
        System.out.println("Booking Date: " + java.time.LocalDate.now());
        System.out.println("\nThank you for choosing HDB!");
    }

    @Override
    public void manageEnquiries(String officerName) {
        String currentProjectName = projectController.getCurrentProjectByOfficer(officerName);
        if (currentProjectName == null) {
            System.out.println("You are not assigned to any project that opens for applying.");
            return;
        }
        
        List<String> enquiryIds = enquiryController.getEnquiriesByProject(currentProjectName);

        if (enquiryIds.isEmpty()) {
            System.out.println("No enquiries found for this project.");
            return;
        }

        System.out.println("\n=== Enquiries for " + currentProjectName + " ===");
        for (int i = 0; i < enquiryIds.size(); i++) {
            String enquiryId = enquiryIds.get(i);
            System.out.println((i + 1) + ". ID: " + enquiryId);
            System.out.println("   Project: " + enquiryController.checkProjectName(enquiryId));
            System.out.println("   Applicant: " + enquiryController.checkApplicantName(enquiryId));
            System.out.println("   Question: " + enquiryController.checkQuestion(enquiryId));
            System.out.println("   Posted: " + enquiryController.checkFormattedCreatedDate(enquiryId));
            
            String answer = enquiryController.checkAnswer(enquiryId);
            if (answer != null) {
                System.out.println("   Answer: " + answer);
                System.out.println("   Answered: " + enquiryController.checkFormattedAnsweredDate(enquiryId));
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
            
            if (choice < 0 || choice > enquiryIds.size()) {
                System.out.println("Invalid selection. Please try again.");
            } else if (choice >= 1 && choice <= enquiryIds.size()) {
                break;
            }
        }

        String selectedEnquiryId = enquiryIds.get(choice - 1);
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
    public int getMenuChoice() {
        System.out.print("Enter your choice: ");
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}