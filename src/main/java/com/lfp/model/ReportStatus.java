package com.lfp.model;

/**
 * Enum representing the status of a report
 */
public enum ReportStatus {
    PENDING("pending"),
    RESOLVED("resolved"),
    REJECTED("rejected");
    
    private final String value;
    
    ReportStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static ReportStatus fromString(String value) {
        for (ReportStatus status : ReportStatus.values()) {
            if (status.value.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return PENDING; // Default to PENDING if not found
    }
    
    @Override
    public String toString() {
        return value;
    }
}
