package entity;

import entity.interfaces.filter.*;
import java.time.LocalDate;
import java.util.List;

public class Filter implements FilterCriteria {
    private String location;
    private List<String> flatTypes;
    private LocalDate openingAfter;
    private LocalDate closingBefore;
    private String manager;
    private String officer;

    @Override
    public String getLocation() {
        return location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public List<String> getFlatTypes() {
        return flatTypes;
    }

    @Override
    public void setFlatTypes(List<String> flatTypes) {
        this.flatTypes = flatTypes;
    }

    @Override
    public LocalDate getOpeningAfter() {
        return openingAfter;
    }

    @Override
    public void setOpeningAfter(LocalDate openingAfter) {
        this.openingAfter = openingAfter;
    }

    @Override
    public LocalDate getClosingBefore() {
        return closingBefore;
    }

    @Override
    public void setClosingBefore(LocalDate closingBefore) {
        this.closingBefore = closingBefore;
    }

    @Override
    public String getManager() {
        return manager;
    }

    @Override
    public void setManager(String manager) {
        this.manager = manager;
    }

    @Override
    public String getOfficer() {
        return officer;
    }

    @Override
    public void setOfficer(String officer) {
        this.officer = officer;
    }
}