package entity;

import entity.interfaces.user.*;

public abstract class User implements UserIdentification, UserAuthentication, Filterable {
    private String name;
    private String nric;
    private int age;
    private String maritalStatus;
    private String password;
    private Filter filter;
    
    public User(String name, String nric, int age, String maritalStatus, String password) {
        this.name = name;
        this.nric = nric;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.password = password;
        this.filter = new Filter();
    }

    @Override
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public String getNric() { return nric; }
    public void setNric(String nric) { this.nric = nric; }

    @Override
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    @Override
    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    @Override
    public String getPassword() { return password; }
    
    @Override
    public void setPassword(String password) { this.password = password; }

    @Override
    public Filter getFilter() {
        return filter;
    }

    @Override
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    @Override
    public abstract String getRole();
}