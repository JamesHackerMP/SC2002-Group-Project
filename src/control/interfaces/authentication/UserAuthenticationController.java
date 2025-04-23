package control.interfaces.authentication;

public interface UserAuthenticationController {
    boolean verifyNric(String nric);
    String authenticate(String nric, String password);
    boolean changePassword(String nric, String currentPassword, String newPassword);
    boolean resetPassword(String nric);
}