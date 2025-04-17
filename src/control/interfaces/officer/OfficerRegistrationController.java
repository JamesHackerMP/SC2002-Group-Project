package control.interfaces.officer;

import entity.HDBOfficer;

public interface OfficerRegistrationController {
    boolean registerForProject(HDBOfficer officer, String projectName);
}