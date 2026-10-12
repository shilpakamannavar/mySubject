package com.study.management.infrastructure.web.dto;

import java.util.List;

public class CognitoGroupInfo {

    private String cognitoGroupName;
    private String configuredAs;
    private boolean foundInCognito;
    private int userCount;
    private List<String> sampleEmails;

    public String getCognitoGroupName()              { return cognitoGroupName; }
    public void setCognitoGroupName(String v)        { this.cognitoGroupName = v; }
    public String getConfiguredAs()                  { return configuredAs; }
    public void setConfiguredAs(String v)            { this.configuredAs = v; }
    public boolean isFoundInCognito()                { return foundInCognito; }
    public void setFoundInCognito(boolean v)         { this.foundInCognito = v; }
    public int getUserCount()                        { return userCount; }
    public void setUserCount(int v)                  { this.userCount = v; }
    public List<String> getSampleEmails()            { return sampleEmails; }
    public void setSampleEmails(List<String> v)      { this.sampleEmails = v; }
}
