package entity;

import entity.interfaces.report.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class Report implements ReportDetails {
    private final String reportId;
    private final String title;
    private final Map<String, String> filters;
    private final List<String[]> data;
    private final LocalDateTime generatedDate;

    public Report(String reportId, String title,
                  Map<String, String> filters, List<String[]> data) {
        this.reportId = reportId;
        this.title = title;
        this.filters = filters;
        this.data = data;
        this.generatedDate = LocalDateTime.now();
    }

    @Override
    public String getReportId() { return reportId; }
    
    @Override
    public String getTitle() { return title; }
    
    @Override
    public Map<String, String> getFilters() { return filters; }
    
    @Override
    public List<String[]> getData() { return data; }
    
    @Override
    public LocalDateTime getGeneratedDate() { return generatedDate; }

    @Override
    public String getSummary() {
        return String.format("Report %s (%s) - %d entries",
                reportId, title, data.size());
    }

    @Override
    public void addDataRow(String[] row) {
        data.add(row);
    }
}