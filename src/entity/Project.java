package entity;

import entity.interfaces.project.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Project implements ProjectDetails, DateBound, OfficerAssignable {
    private String name;
    private String neighborhood;
    private int twoRoomUnits;
    private int twoRoomPrice;
    private int threeRoomUnits;
    private int threeRoomPrice;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private String manager;
    private int officerSlots;
    private final List<String> officers;
    private boolean visible;
    private final List<Flat> flats;
    private final List<String> pendingOfficers = new ArrayList<>();

    public Project(String name, String neighborhood, int twoRoomUnits, int twoRoomPrice,
                   int threeRoomUnits, int threeRoomPrice, LocalDate openingDate,
                   LocalDate closingDate, String manager, int officerSlots) {
        this.name = name;
        this.neighborhood = neighborhood;
        this.twoRoomUnits = twoRoomUnits;
        this.twoRoomPrice = twoRoomPrice;
        this.threeRoomUnits = threeRoomUnits;
        this.threeRoomPrice = threeRoomPrice;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.manager = manager;
        this.officerSlots = officerSlots;
        this.officers = new ArrayList<>();
        this.visible = true;
        this.flats = new ArrayList<>();
        initializeFlats();
    }

    private void initializeFlats() {
        for (int i = 1; i <= twoRoomUnits; i++) {
            flats.add(new Flat(name, "2-Room", i, twoRoomPrice));
        }
        for (int i = 1; i <= threeRoomUnits; i++) {
            flats.add(new Flat(name, "3-Room", i, threeRoomPrice));
        }
    }

    public boolean bookFlat(String flatType, String applicantName, String officerName) {

        for (Flat flat : flats) {
            if (flat.getType().equals(flatType) && !flat.isBooked()) {
                flat.setBooked(true);
                flat.setBookedBy(applicantName);
                if (flatType.equals("2-Room")) {
                    twoRoomUnits--;
                } else if (flatType.equals("3-Room")) {
                    threeRoomUnits--;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Project Name: " + name +
               ", Neighborhood: " + neighborhood +
               ", 2-Room Units: " + twoRoomUnits +
               ", 3-Room Units: " + threeRoomUnits +
               ", Opening Date: " + openingDate +
               ", Closing Date: " + closingDate +
               ", Manager: " + manager;
    }

    @Override
    public String getName() { return name; }

    @Override
    public void setName(String name) { this.name = name; }

    @Override
    public String getNeighborhood() { return neighborhood; }

    @Override
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    @Override
    public int getTwoRoomUnits() { return twoRoomUnits; }

    @Override
    public void setTwoRoomUnits(int twoRoomUnits) { this.twoRoomUnits = twoRoomUnits; }

    @Override
    public int getTwoRoomPrice() { return twoRoomPrice; }

    @Override
    public void setTwoRoomPrice(int twoRoomPrice) { this.twoRoomPrice = twoRoomPrice; }

    @Override
    public int getThreeRoomUnits() { return threeRoomUnits; }

    @Override
    public void setThreeRoomUnits(int threeRoomUnits) { this.threeRoomUnits = threeRoomUnits; }

    @Override
    public int getThreeRoomPrice() { return threeRoomPrice; }

    @Override
    public void setThreeRoomPrice(int threeRoomPrice) { this.threeRoomPrice = threeRoomPrice; }

    @Override
    public LocalDate getOpeningDate() { return openingDate; }

    @Override
    public void setOpeningDate(LocalDate openingDate) { this.openingDate = openingDate; }

    @Override
    public LocalDate getClosingDate() { return closingDate; }

    @Override
    public void setClosingDate(LocalDate closingDate) { this.closingDate = closingDate; }

    public String getManager() { return manager; }

    public void setManager(String manager) { this.manager = manager; }

    @Override
    public int getOfficerSlots() { return officerSlots; }

    @Override
    public void setOfficerSlots(int officerSlots) { this.officerSlots = officerSlots; }

    public boolean isVisible() { return visible; }

    public void setVisible(boolean visible) { this.visible = visible; }

    @Override
    public List<Flat> getFlats() { return flats; }

    @Override
    public List<String> getOfficers() { return officers; }

    @Override
    public List<String> getPendingOfficers() { return pendingOfficers; }

    @Override
    public void addOfficer(String officerName) {
        if (officers.size() < officerSlots && !officers.contains(officerName)) {
            officers.add(officerName);
        }
    }

    @Override
    public void removeOfficer(String officerName) {
        officers.remove(officerName);
    }

    @Override
    public void addPendingOfficer(String name) {
        if (!pendingOfficers.contains(name)) {
            pendingOfficers.add(name);
        }
    }

    @Override
    public boolean removePendingOfficer(String name) {
        return pendingOfficers.remove(name);
    }

    @Override
    public boolean isOpenForApplication() {
        LocalDate today = LocalDate.now();
        return !today.isBefore(openingDate) && !today.isAfter(closingDate);
    }
}