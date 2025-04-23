package control.interfaces.manager;

import entity.Report;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportGenerationController {
    Report generateReport(Map<String, String> filters);
    boolean generateApplicationsReport(Map<String, String> filters);
    String getReportId();
    String getReportTitle();
    LocalDateTime getReportGeneratedDate();
    List<String[]> getReportData();
    boolean hasReportData();
}