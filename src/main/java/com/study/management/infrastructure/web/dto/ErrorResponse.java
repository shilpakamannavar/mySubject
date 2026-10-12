package com.study.management.infrastructure.web.dto;

import java.time.OffsetDateTime;

public class ErrorResponse {

    private String error;
    private String message;
    private String details;
    private OffsetDateTime timestamp;
    private String path;

    public ErrorResponse() {
        this.timestamp = OffsetDateTime.now();
    }

    public ErrorResponse(String error, String message, String path) {
        this();
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public String getError()                    { return error; }
    public void setError(String error)          { this.error = error; }
    public String getMessage()                  { return message; }
    public void setMessage(String message)      { this.message = message; }
    public String getDetails()                  { return details; }
    public void setDetails(String details)      { this.details = details; }
    public OffsetDateTime getTimestamp()        { return timestamp; }
    public void setTimestamp(OffsetDateTime v)  { this.timestamp = v; }
    public String getPath()                     { return path; }
    public void setPath(String path)            { this.path = path; }
}
