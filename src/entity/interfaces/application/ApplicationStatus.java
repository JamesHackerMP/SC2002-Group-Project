package entity.interfaces.application;

import entity.Application;

public interface ApplicationStatus {
    Application.Status getStatus();
    void setStatus(Application.Status status);
}