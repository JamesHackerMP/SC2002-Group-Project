package control.interfaces.application;

import entity.Application;
import java.util.List;

public interface ApplicationQueryController {
    List<Application> getAllApplications();
}