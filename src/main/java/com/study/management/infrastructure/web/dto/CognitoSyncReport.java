package com.study.management.infrastructure.web.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public class CognitoSyncReport {

    private OffsetDateTime syncedAt;
    private int totalUsersScanned;
    private Map<String, RoleSyncSummary> summary;
    private List<SyncDetail> details;

    public OffsetDateTime getSyncedAt()                 { return syncedAt; }
    public void setSyncedAt(OffsetDateTime v)           { this.syncedAt = v; }
    public int getTotalUsersScanned()                   { return totalUsersScanned; }
    public void setTotalUsersScanned(int v)             { this.totalUsersScanned = v; }
    public Map<String, RoleSyncSummary> getSummary()    { return summary; }
    public void setSummary(Map<String, RoleSyncSummary> v) { this.summary = v; }
    public List<SyncDetail> getDetails()                { return details; }
    public void setDetails(List<SyncDetail> v)          { this.details = v; }
}
