package com.lfp.model;

/**
 * Enum representing the status of a place
 */
public enum PlaceStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");
    
    private final String value;
    
    PlaceStatus(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    public static PlaceStatus fromString(String value) {
        for (PlaceStatus status : PlaceStatus.values()) {
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
