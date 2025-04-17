package control.interfaces.filter;

import entity.Filter;
import entity.Project;
import entity.User;
import java.util.List;

public interface FilterManagementController {
    void setCurrentUser(User user);
    Filter getFilter();
    List<Project> applyFilters(List<Project> projects);
}