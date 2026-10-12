package com.study.management.infrastructure.web.dto;

import java.time.LocalDate;

public class CreateSubjectRequest {

    private String    firstName;
    private String    lastName;
    private String    initials;
    private LocalDate dateOfBirth;
    private String    gender;
    private String    email;
    private String    phone;

    public String getFirstName()               { return firstName; }
    public void setFirstName(String v)         { this.firstName = v; }
    public String getLastName()                { return lastName; }
    public void setLastName(String v)          { this.lastName = v; }
    public String getInitials()                { return initials; }
    public void setInitials(String v)          { this.initials = v; }
    public LocalDate getDateOfBirth()          { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v)    { this.dateOfBirth = v; }
    public String getGender()                  { return gender; }
    public void setGender(String v)            { this.gender = v; }
    public String getEmail()                   { return email; }
    public void setEmail(String v)             { this.email = v; }
    public String getPhone()                   { return phone; }
    public void setPhone(String v)             { this.phone = v; }
}