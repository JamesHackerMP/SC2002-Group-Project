package control.interfaces.manager;

import entity.Report;
import java.util.Map;

public interface ReportGenerationController {
    Report generateApplicationsReport(Map<String, String> filters);
}