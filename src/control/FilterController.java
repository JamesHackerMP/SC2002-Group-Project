package control;

import control.interfaces.filter.*;
import entity.Filter;
import entity.Project;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class FilterController implements FilterManagementController {
    private String currentUserName;
    private final AuthenticationController authController;
    private final ProjectController projectController;


    public FilterController(AuthenticationController authController, ProjectController projectController) {
        this.currentUserName = null;
        this.authController = authController;
        this.projectController = projectController;
    }

    @Override
    public void setCurrentUser(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    @Override
    public Filter getFilter() {
        return authController.getUser(currentUserName).getFilter();
    }

    @Override
    public List<String> applyFilters(List<String> projectNames) {

        Filter filter = authController.getUser(currentUserName).getFilter();
        return projectNames.stream()
                .map(projectName -> projectController.getProject(projectName))
                .filter(p -> {
                    String neighborhood = filter.getNeighborhood();
                    return neighborhood == null || p.getNeighborhood().equalsIgnoreCase(neighborhood);
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
                .map(Project::getName)
                .collect(Collectors.toList());
    }

    @Override
    public String checkNeighborhood() {
        return getFilter().getNeighborhood();
    }

    @Override
    public List<String> checkFlatTypes() {
        return getFilter().getFlatTypes();
    }

    @Override
    public LocalDate checkOpeningAfter() {
        return getFilter().getOpeningAfter();
    }

    @Override
    public LocalDate checkClosingBefore() {
        return getFilter().getClosingBefore();
    }

    @Override
    public String checkManager() {
        return getFilter().getManager();
    }

    @Override
    public String checkOfficer() {
        return getFilter().getOfficer();
    }

    @Override
    public void updateNeighborhood(String neighborhood) {
        getFilter().setNeighborhood(neighborhood);
    }

    @Override
    public void updateFlatTypes(List<String> flatTypes) {
        getFilter().setFlatTypes(flatTypes);
    }

    @Override
    public void updateOpeningAfter(LocalDate openingAfter) {
        getFilter().setOpeningAfter(openingAfter);
    }

    @Override
    public void updateClosingBefore(LocalDate closingBefore) {
        getFilter().setClosingBefore(closingBefore);
    }

    @Override
    public void updateManager(String manager) {
        getFilter().setManager(manager);
    }

    @Override
    public void updateOfficer(String officer) {
        getFilter().setOfficer(officer);
    }

    @Override
    public void updateAllFilters() {
        getFilter().setNeighborhood(null);
        getFilter().setFlatTypes(null);
        getFilter().setOpeningAfter(null);
        getFilter().setClosingBefore(null);
        getFilter().setManager(null);
        getFilter().setOfficer(null);
    }
}