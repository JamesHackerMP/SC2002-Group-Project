package util;

import entity.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface FileDataHandler {
    List<String[]> readCSV(String filePath) throws IOException;
    void writeCSV(String filePath, List<String[]> data, String[] headers) throws IOException;
    void saveUsers(Map<String, User> users) throws IOException;
    void saveProjects(List<Project> projects) throws IOException;
    List<User> loadApplicants() throws IOException;
    List<User> loadOfficers() throws IOException;
    List<User> loadManagers() throws IOException;
    List<Project> loadProjects() throws IOException;
}