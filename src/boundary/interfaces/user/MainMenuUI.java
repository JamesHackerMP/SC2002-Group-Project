package boundary.interfaces.user;

import entity.User;

public interface MainMenuUI {
    void displayMainMenu(User user);
    int getMenuChoice();
}