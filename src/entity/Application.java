package entity;

import entity.interfaces.application.*;

public class Application implements ApplicationDetails, ApplicationStatus, FlatAssignment {
    public enum Status { PENDING, SUCCESSFUL, UNSUCCESSFUL, BOOKED }
    
    private String applicantName;
    private String projectName;
    private String flatTypeApply;
    private Status status;
    private String flatType;
    private int price;

    public Application(String applicantName, String projectName, String flatTypeApply) {
        this.applicantName = applicantName;
        this.projectName = projectName;
        this.flatTypeApply = flatTypeApply;
        this.status = Status.PENDING;
    }

    @Override
    public String getApplicantName() { return applicantName; }
    
    @Override
    public void setApplicantName(String applicantName) { this.applicantName = applicantName; }

    @Override
    public String getProjectName() { return projectName; }
    
    @Override
    public void setProjectName(String projectName) { this.projectName = projectName; }

    @Override
    public String getFlatTypeApply() { return flatTypeApply; }
    
    @Override
    public void setFlatTypeApply(String flatTypeApply) { this.flatTypeApply = flatTypeApply; }

    @Override
    public Status getStatus() { return status; }
    
    @Override
    public void setStatus(Status status) { this.status = status; }

    @Override
    public String getFlatType() { return flatType; }
    
    @Override
    public void setFlatType(String flatType) { this.flatType = flatType; }

    @Override
    public int getPrice() { return price; }
    
    @Override
    public void setPrice(int price) { this.price = price; }

}