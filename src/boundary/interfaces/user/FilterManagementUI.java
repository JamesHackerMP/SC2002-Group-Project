package boundary.interfaces.user;

import java.util.List;

public interface FilterManagementUI {
    void setFilters();
    List<String> applyFilters(List<String> projectNames);
}