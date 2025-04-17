package boundary.interfaces.user;

import entity.Project;
import java.util.List;

public interface FilterManagementUI {
    void setFilters();
    List<Project> applyFilters(List<Project> projects);
}