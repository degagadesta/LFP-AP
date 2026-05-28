package com.lfp.rmi;

import com.lfp.model.Place;
import com.lfp.model.PlaceStatus;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI Service interface for Place operations
 * Remote interface for distributed place management
 */
public interface PlaceService extends Remote {
    
    /**
     * Get all places
     * @return List of all places
     * @throws RemoteException if RMI communication fails
     */
    List<Place> getAllPlaces() throws RemoteException;
    
    /**
     * Get places by status
     * @param status Place status (PENDING, APPROVED, REJECTED)
     * @return List of places with specified status
     * @throws RemoteException if RMI communication fails
     */
    List<Place> getPlacesByStatus(PlaceStatus status) throws RemoteException;
    
    /**
     * Get place by ID
     * @param placeId Place ID
     * @return Place object or null if not found
     * @throws RemoteException if RMI communication fails
     */
    Place getPlaceById(int placeId) throws RemoteException;
    
    /**
     * Add a new place
     * @param place Place object to add
     * @return ID of newly created place
     * @throws RemoteException if RMI communication fails
     */
    int addPlace(Place place) throws RemoteException;
    
    /**
     * Update place information
     * @param place Place object with updated information
     * @return true if update successful
     * @throws RemoteException if RMI communication fails
     */
    boolean updatePlace(Place place) throws RemoteException;
    
    /**
     * Delete a place
     * @param placeId Place ID to delete
     * @return true if deletion successful
     * @throws RemoteException if RMI communication fails
     */
    boolean deletePlace(int placeId) throws RemoteException;
    
    /**
     * Approve a pending place (admin only)
     * @param placeId Place ID to approve
     * @return true if approval successful
     * @throws RemoteException if RMI communication fails
     */
    boolean approvePlace(int placeId) throws RemoteException;
    
    /**
     * Reject a pending place (admin only)
     * @param placeId Place ID to reject
     * @return true if rejection successful
     * @throws RemoteException if RMI communication fails
     */
    boolean rejectPlace(int placeId) throws RemoteException;
    
    /**
     * Search places by name or category
     * @param query Search query
     * @return List of matching places
     * @throws RemoteException if RMI communication fails
     */
    List<Place> searchPlaces(String query) throws RemoteException;
    
    /**
     * Get places by category
     * @param category Category name (Cafe, Library, Coworking, etc.)
     * @return List of places in specified category
     * @throws RemoteException if RMI communication fails
     */
    List<Place> getPlacesByCategory(String category) throws RemoteException;
    
    /**
     * Get user's favorite places
     * @param userId User ID
     * @return List of favorite places
     * @throws RemoteException if RMI communication fails
     */
    List<Place> getUserFavorites(int userId) throws RemoteException;
    
    /**
     * Add place to user's favorites
     * @param userId User ID
     * @param placeId Place ID
     * @return true if added successfully
     * @throws RemoteException if RMI communication fails
     */
    boolean addToFavorites(int userId, int placeId) throws RemoteException;
    
    /**
     * Remove place from user's favorites
     * @param userId User ID
     * @param placeId Place ID
     * @return true if removed successfully
     * @throws RemoteException if RMI communication fails
     */
    boolean removeFromFavorites(int userId, int placeId) throws RemoteException;
    
    /**
     * Check if place is in user's favorites
     * @param userId User ID
     * @param placeId Place ID
     * @return true if place is favorited
     * @throws RemoteException if RMI communication fails
     */
    boolean isFavorite(int userId, int placeId) throws RemoteException;
    
    /**
     * Get total number of places
     * @return Total place count
     * @throws RemoteException if RMI communication fails
     */
    int getPlaceCount() throws RemoteException;
    
    /**
     * Get number of pending places
     * @return Pending place count
     * @throws RemoteException if RMI communication fails
     */
    int getPendingPlaceCount() throws RemoteException;
}
