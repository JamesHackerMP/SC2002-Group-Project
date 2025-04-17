package boundary.interfaces.applicant;

import entity.User;

public interface ApplicantMenuUI {
    void displayMenu(User user);
    int getMenuChoice();
}