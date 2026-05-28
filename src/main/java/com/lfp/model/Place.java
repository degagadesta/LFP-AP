package com.lfp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Place model class representing a laptop-friendly location
 * Implements Serializable for RMI transmission
 */
public class Place implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private String name;
    private String description;
    private String category;
    private String address;
    private double locationLat;
    private double locationLng;
    private double ratingWifi;
    private double ratingPower;
    private double ratingService;
    private double ratingOverall;
    private PlaceStatus status;
    private Integer contributedBy;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public Place() {
        this.status = PlaceStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }
    
    public Place(String name, String description, String category, String address) {
        this();
        this.name = name;
        this.description = description;
        this.category = category;
        this.address = address;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public double getLocationLat() {
        return locationLat;
    }
    
    public void setLocationLat(double locationLat) {
        this.locationLat = locationLat;
    }
    
    public double getLocationLng() {
        return locationLng;
    }
    
    public void setLocationLng(double locationLng) {
        this.locationLng = locationLng;
    }
    
    public double getRatingWifi() {
        return ratingWifi;
    }
    
    public void setRatingWifi(double ratingWifi) {
        this.ratingWifi = ratingWifi;
        updateOverallRating();
    }
    
    public double getRatingPower() {
        return ratingPower;
    }
    
    public void setRatingPower(double ratingPower) {
        this.ratingPower = ratingPower;
        updateOverallRating();
    }
    
    public double getRatingService() {
        return ratingService;
    }
    
    public void setRatingService(double ratingService) {
        this.ratingService = ratingService;
        updateOverallRating();
    }
    
    public double getRatingOverall() {
        return ratingOverall;
    }
    
    public void setRatingOverall(double ratingOverall) {
        this.ratingOverall = ratingOverall;
    }
    
    public PlaceStatus getStatus() {
        return status;
    }
    
    public void setStatus(PlaceStatus status) {
        this.status = status;
    }
    
    public Integer getContributedBy() {
        return contributedBy;
    }
    
    public void setContributedBy(Integer contributedBy) {
        this.contributedBy = contributedBy;
    }
    
    public String getImageUrl() {
        return imageUrl;
    }
    
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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
    
    // Utility methods
    private void updateOverallRating() {
        this.ratingOverall = (ratingWifi + ratingPower + ratingService) / 3.0;
        this.ratingOverall = Math.round(ratingOverall * 10.0) / 10.0; // Round to 1 decimal
    }
    
    public boolean isApproved() {
        return status == PlaceStatus.APPROVED;
    }
    
    public boolean isPending() {
        return status == PlaceStatus.PENDING;
    }
    
    public String getStatusBadge() {
        switch (status) {
            case APPROVED:
                return "✓ Approved";
            case PENDING:
                return "⏳ Pending";
            case REJECTED:
                return "✗ Rejected";
            default:
                return "Unknown";
        }
    }
    
    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", status=" + status +
                ", ratingOverall=" + ratingOverall +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Place place = (Place) o;
        return id == place.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
