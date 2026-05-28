package com.lfp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Report model class representing a user report about a place
 * Implements Serializable for RMI transmission
 */
public class Report implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int placeId;
    private int userId;
    private String reason;
    private ReportStatus status;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for display purposes (not in DB)
    private String placeName;
    private String reportedBy;
    
    // Constructors
    public Report() {
        this.status = ReportStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public Report(int placeId, int userId, String reason) {
        this();
        this.placeId = placeId;
        this.userId = userId;
        this.reason = reason;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getPlaceId() {
        return placeId;
    }
    
    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public ReportStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReportStatus status) {
        this.status = status;
    }
    
    public String getAdminNotes() {
        return adminNotes;
    }
    
    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getPlaceName() {
        return placeName;
    }
    
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
    
    public String getReportedBy() {
        return reportedBy;
    }
    
    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }
    
    // Utility methods
    public boolean isPending() {
        return status == ReportStatus.PENDING;
    }
    
    public boolean isResolved() {
        return status == ReportStatus.RESOLVED;
    }
    
    public String getStatusBadge() {
        switch (status) {
            case PENDING:
                return "⏳ Pending";
            case RESOLVED:
                return "✓ Resolved";
            case REJECTED:
                return "✗ Rejected";
            default:
                return "Unknown";
        }
    }
    
    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", placeId=" + placeId +
                ", userId=" + userId +
                ", status=" + status +
                ", reason='" + reason + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Report report = (Report) o;
        return id == report.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
