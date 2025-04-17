package entity.interfaces.project;

import java.time.LocalDate;

public interface DateBound {
    LocalDate getOpeningDate();
    LocalDate getClosingDate();
    boolean isOpenForApplication();
    void setOpeningDate(LocalDate openingDate);
    void setClosingDate(LocalDate closingDate);
}