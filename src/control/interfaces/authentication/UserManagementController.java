package control.interfaces.authentication;

import entity.User;

public interface UserManagementController {
    User getUser(String name);
    String checkRole(String userName);
    String checkMaritalStatus(String userName);
    String checkNric(String userName);
    int checkAge(String userName);
    boolean isValidNric(String nric);
    boolean userExists(String nric);
    int getUserCountByRole(String role);
}