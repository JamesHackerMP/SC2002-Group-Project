package boundary.interfaces.officer;

import entity.HDBOfficer;

public interface MenuDisplayUI {
    void displayMenu(HDBOfficer officer);
    int getMenuChoice();
}