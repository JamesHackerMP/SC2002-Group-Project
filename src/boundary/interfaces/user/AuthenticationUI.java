package boundary.interfaces.user;

import entity.User;

public interface AuthenticationUI {
    User login();
    boolean changePassword(User user);
}