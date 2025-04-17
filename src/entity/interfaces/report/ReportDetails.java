package entity.interfaces.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ReportDetails {
    String getReportId();
    String getTitle();
    Map<String, String> getFilters();
    List<String[]> getData();
    LocalDateTime getGeneratedDate();
    String getSummary();
    void addDataRow(String[] row);
}