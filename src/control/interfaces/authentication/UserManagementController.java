package control.interfaces.authentication;

import entity.User;

public interface UserManagementController {
    User getUser(String name);
    boolean isValidNric(String nric);
    boolean userExists(String nric);
    int getUserCountByRole(String role);
}