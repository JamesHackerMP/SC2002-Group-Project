package boundary.interfaces.manager;

import entity.HDBManager;
import entity.Project;

public interface ApplicationProcessingUI {
    void processApplications(HDBManager manager);
    void processPendingApplications(Project project);
}