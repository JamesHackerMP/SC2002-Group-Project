package control;

import control.interfaces.filter.*;
import entity.Filter;
import entity.Project;
import entity.User;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FilterController implements FilterManagementController {
    private User currentUser;

    public FilterController() {
        this.currentUser = null;
    }

    @Override
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @Override
    public Filter getFilter() {
        return currentUser.getFilter();
    }

    @Override
    public List<Project> applyFilters(List<Project> projects) {
        Filter filter = currentUser.getFilter();
        return projects.stream()
                .filter(p -> {
                    String location = filter.getLocation();
                    return location == null || p.getNeighborhood().equalsIgnoreCase(location);
                })
                .filter(p -> {
                    List<String> flatTypes = filter.getFlatTypes();
                    if (flatTypes == null || flatTypes.isEmpty()) return true;
                    return p.getFlats().stream().anyMatch(f -> flatTypes.contains(f.getType()));
                })
                .filter(p -> {
                    LocalDate openingAfter = filter.getOpeningAfter();
                    return openingAfter == null || !p.getOpeningDate().isBefore(openingAfter);
                })
                .filter(p -> {
                    LocalDate closingBefore = filter.getClosingBefore();
                    return closingBefore == null || !p.getClosingDate().isAfter(closingBefore);
                })
                .filter(p -> {
                    String manager = filter.getManager();
                    return manager == null || p.getManager().equalsIgnoreCase(manager);
                })
                .filter(p -> {
                    String officer = filter.getOfficer();
                    return officer == null || p.getOfficers().contains(officer);
                })
                .sorted(Comparator.comparing(Project::getName))
                .collect(Collectors.toList());
    }
}