package boundary.interfaces.officer;

import entity.HDBOfficer;

public interface BookingManagementUI {
    void processFlatBooking(HDBOfficer officer);
    void generateBookingReceipt(HDBOfficer officer);
}