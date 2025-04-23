package boundary.interfaces.user;

public interface AuthenticationUI {
    String login();
    boolean changePassword(String userName);
}