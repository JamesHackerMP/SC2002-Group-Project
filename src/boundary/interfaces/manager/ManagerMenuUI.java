package boundary.interfaces.manager;

import entity.HDBManager;

public interface ManagerMenuUI {
    void displayMenu(HDBManager manager);
    int getMenuChoice();
}