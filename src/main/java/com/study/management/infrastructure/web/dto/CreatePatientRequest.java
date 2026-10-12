package com.study.management.infrastructure.web.dto;

import com.study.management.domain.model.PatientGender;

import java.time.LocalDate;

public class CreatePatientRequest {

    private String firstName;
    private String lastName;
    private String initials;
    private LocalDate dateOfBirth;
    private PatientGender gender;
    private String race;
    private String ethnicity;
    private String phone;
    private String email;
    private String emergencyContact;
    private String emergencyPhone;

    public String getFirstName()                    { return firstName; }
    public void setFirstName(String v)             { this.firstName = v; }
    public String getLastName()                     { return lastName; }
    public void setLastName(String v)              { this.lastName = v; }
    public String getInitials()                     { return initials; }
    public void setInitials(String v)              { this.initials = v; }
    public LocalDate getDateOfBirth()               { return dateOfBirth; }
    public void setDateOfBirth(LocalDate v)        { this.dateOfBirth = v; }
    public PatientGender getGender()                { return gender; }
    public void setGender(PatientGender v)         { this.gender = v; }
    public String getRace()                         { return race; }
    public void setRace(String v)                  { this.race = v; }
    public String getEthnicity()                    { return ethnicity; }
    public void setEthnicity(String v)             { this.ethnicity = v; }
    public String getPhone()                        { return phone; }
    public void setPhone(String v)                 { this.phone = v; }
    public String getEmail()                        { return email; }
    public void setEmail(String v)                 { this.email = v; }
    public String getEmergencyContact()             { return emergencyContact; }
    public void setEmergencyContact(String v)      { this.emergencyContact = v; }
    public String getEmergencyPhone()               { return emergencyPhone; }
    public void setEmergencyPhone(String v)        { this.emergencyPhone = v; }
}
