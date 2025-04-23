import boundary.*;
import control.*;
import entity.*;
import util.*;

public class Main {
    private final AuthenticationController authController;
    private final FilterController filterController;
    private final ProjectController projectController;
    private final ApplicationController applicationController;
    private final EnquiryController enquiryController;
    private final OfficerController officerController;
    private final ManagerController managerController;

    private final UserUI userUI;
    private final ApplicantUI applicantUI;
    private final HDBOfficerUI officerUI;
    private final HDBManagerUI managerUI;

    private String currentUserName;

    public Main() {

        FileDataHandler fileDataHandler = new CSVFileHandler();
        
        this.authController = new AuthenticationController(fileDataHandler);
        this.projectController = new ProjectController(fileDataHandler);
        this.filterController = new FilterController(authController, projectController);
        this.applicationController = new ApplicationController(projectController, authController);
        this.enquiryController = new EnquiryController();
        this.officerController = new OfficerController(projectController, applicationController);
        this.managerController = new ManagerController(projectController, applicationController, officerController, authController);

        this.userUI = new UserUI(authController, filterController, applicationController, projectController, enquiryController);
        this.applicantUI = new ApplicantUI(projectController, applicationController, enquiryController, authController, filterController);
        this.officerUI = new HDBOfficerUI(projectController, applicationController, enquiryController, officerController,
                authController, filterController);
        this.managerUI = new HDBManagerUI(projectController, applicationController, enquiryController, managerController, authController, filterController);
    }

    public void run() {
        System.out.println("=== HDB BTO Management System ===");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            authController.saveUsers();
            projectController.saveProjects();
            System.out.println("Data saved successfully.");
        }));

        while (true) {
            currentUserName = userUI.login();
            
            if (authController.getUser(currentUserName) != null) {
                mainMenuLoop();
            }
        }
    }

    private void mainMenuLoop() {
        while (true) {
            userUI.displayMainMenu(currentUserName);
            int choice = userUI.getMenuChoice();

            switch (choice) {
                case 1 -> userUI.displayProfile(currentUserName);
                case 2 -> userUI.changePassword(currentUserName);
                case 3 -> userUI.setFilters();
                case 4 -> handleRoleSpecificMenu();
                case 5 -> {
                    if (authController.getUser(currentUserName) instanceof HDBOfficer) {
                        applicantUI.displayMenu(currentUserName);
                    }
                }
                case 0 -> {
                    System.out.println("Logging out...");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void handleRoleSpecificMenu() {
        if (authController.getUser(currentUserName) instanceof Applicant) {
            applicantUI.displayMenu(currentUserName);
        } else if (authController.getUser(currentUserName) instanceof HDBOfficer officer) {
            officerUI.displayMenu(officer.getName());
        } else if (authController.getUser(currentUserName) instanceof HDBManager manager) {
            managerUI.displayMenu(manager.getName());
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }
}