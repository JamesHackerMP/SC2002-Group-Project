package entity;

import entity.interfaces.flat.*;
import java.time.LocalDate;

public class Flat implements BookableFlat, FlatIdentification  {
    private String id;
    private final String projectName;
    private final String type;
    private final int number;
    private final int price;
    private boolean isBooked;
    private String bookedBy;
    private LocalDate bookingDate;

    public Flat(String projectName, String type, int number, int price) {
        this.projectName = projectName;
        this.type = type;
        this.number = number;
        this.price = price;
        this.isBooked = false;
    }

    @Override
    public String getId() { return id; }

    @Override
    public void setId(String id) { this.id = id; }

    public String getProjectName() { return projectName; }
    
    @Override
    public String getType() { return type; }
    
    @Override
    public int getNumber() { return number; }
    
    @Override
    public int getPrice() { return price; }
    
    @Override
    public boolean isBooked() { return isBooked; }
    
    @Override
    public void setBooked(boolean booked) { isBooked = booked; }
    
    @Override
    public String getBookedBy() { return bookedBy; }
    
    @Override
    public void setBookedBy(String bookedBy) {
        this.bookedBy = bookedBy;
    }
    
    @Override
    public LocalDate getBookingDate() { return bookingDate; }
    
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public void bookFlat(String applicantName) {
        this.isBooked = true;
        this.bookedBy = applicantName;
        this.bookingDate = LocalDate.now();
    }

    @Override
    public String toString() {
        return String.format("%s %s-%d (%s)",
                projectName, type, number,
                isBooked ? "Booked by " + bookedBy : "Available");
    }
}