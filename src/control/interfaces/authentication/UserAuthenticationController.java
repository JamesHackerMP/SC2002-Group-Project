package control.interfaces.authentication;

import entity.User;

public interface UserAuthenticationController {
    boolean checkNric(String nric);
    User authenticate(String nric, String password);
    boolean changePassword(String nric, String currentPassword, String newPassword);
    boolean resetPassword(String nric);
}