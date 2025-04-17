package entity.interfaces.flat;

import java.time.LocalDate;

public interface BookableFlat {
    String getType();
    int getNumber();
    int getPrice();
    boolean isBooked();
    String getBookedBy();
    LocalDate getBookingDate();
    void setBooked(boolean isBooked);
    void setBookedBy(String applicantName);
}