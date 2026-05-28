package com.lfp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Favorite model class representing a user's favorite place
 * Implements Serializable for RMI transmission
 */
public class Favorite implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int id;
    private int userId;
    private int placeId;
    private LocalDateTime createdAt;
    
    // Additional field for display (not in DB)
    private Place place;
    
    // Constructors
    public Favorite() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Favorite(int userId, int placeId) {
        this();
        this.userId = userId;
        this.placeId = placeId;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getPlaceId() {
        return placeId;
    }
    
    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public Place getPlace() {
        return place;
    }
    
    public void setPlace(Place place) {
        this.place = place;
    }
    
    @Override
    public String toString() {
        return "Favorite{" +
                "id=" + id +
                ", userId=" + userId +
                ", placeId=" + placeId +
                ", createdAt=" + createdAt +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Favorite favorite = (Favorite) o;
        return id == favorite.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}
