package entity.interfaces.filter;

import java.time.LocalDate;
import java.util.List;

public interface FilterCriteria {
    String getLocation();
    void setLocation(String location);
    List<String> getFlatTypes();
    void setFlatTypes(List<String> flatTypes);
    LocalDate getOpeningAfter();
    void setOpeningAfter(LocalDate openingAfter);
    LocalDate getClosingBefore();
    void setClosingBefore(LocalDate closingBefore);
    String getManager();
    void setManager(String manager);
    String getOfficer();
    void setOfficer(String officer);
}