package control.interfaces.filter;

import entity.Filter;
import java.time.LocalDate;
import java.util.List;

public interface FilterManagementController {
    void setCurrentUser(String userName);
    Filter getFilter();
    List<String> applyFilters(List<String> userName);
    String checkNeighborhood();
    List<String> checkFlatTypes();
    LocalDate checkOpeningAfter();
    LocalDate checkClosingBefore();
    String checkOfficer();
    String checkManager();
    void updateNeighborhood(String neighborhood);
    void updateFlatTypes(List<String> flatTypes);
    void updateOpeningAfter(LocalDate openingAfter);
    void updateClosingBefore(LocalDate closingBefore);
    void updateManager(String manager);
    void updateOfficer(String officer);
    void updateAllFilters();  
}