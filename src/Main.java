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

    private User currentUser;

    public Main() {

        FileDataHandler fileDataHandler = new CSVFileHandler();
        
        this.authController = new AuthenticationController(fileDataHandler);
        this.filterController = new FilterController();
        this.projectController = new ProjectController(fileDataHandler);
        this.applicationController = new ApplicationController(projectController);
        this.enquiryController = new EnquiryController();
        this.officerController = new OfficerController(projectController, applicationController);
        this.managerController = new ManagerController(projectController, applicationController, officerController, authController);

        this.userUI = new UserUI(authController, filterController, applicationController, projectController, enquiryController);
        this.applicantUI = new ApplicantUI(projectController, applicationController, enquiryController, filterController);
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
            currentUser = userUI.login();
            if (currentUser != null) {
                mainMenuLoop();
            }
        }
    }

    private void mainMenuLoop() {
        while (true) {
            userUI.displayMainMenu(currentUser);
            int choice = userUI.getMenuChoice();

            switch (choice) {
                case 1 -> userUI.displayProfile(currentUser);
                case 2 -> userUI.changePassword(currentUser);
                case 3 -> userUI.setFilters();
                case 4 -> handleRoleSpecificMenu();
                case 5 -> {
                    if (currentUser instanceof HDBOfficer) {
                        applicantUI.displayMenu(currentUser);
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
        if (currentUser instanceof Applicant) {
            applicantUI.displayMenu(currentUser);
        } else if (currentUser instanceof HDBOfficer officer) {
            officerUI.displayMenu(officer);
        } else if (currentUser instanceof HDBManager manager) {
            managerUI.displayMenu(manager);
        }
    }

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }
}