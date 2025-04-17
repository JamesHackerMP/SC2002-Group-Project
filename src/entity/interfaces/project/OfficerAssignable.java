package entity.interfaces.project;

import java.util.List;

public interface OfficerAssignable {
    List<String> getOfficers();
    List<String> getPendingOfficers();
    int getOfficerSlots();
    void setOfficerSlots(int officerSlots);
    void addOfficer(String officerName);
    void removeOfficer(String officerName);
    void addPendingOfficer(String name);
    boolean removePendingOfficer(String name);
}