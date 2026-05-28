package com.lfp.rmi;

import com.lfp.model.Place;
import com.lfp.model.PlaceStatus;
import com.lfp.util.DatabaseUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PlaceService RMI interface
 * Handles all place-related database operations
 */
public class PlaceServiceImpl extends UnicastRemoteObject implements PlaceService {
    private static final long serialVersionUID = 1L;
    
    public PlaceServiceImpl() throws RemoteException {
        super();
    }
    
    @Override
    public List<Place> getAllPlaces() throws RemoteException {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT * FROM places ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                places.add(extractPlaceFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + places.size() + " places");
            return places;
            
        } catch (SQLException e) {
            System.err.println("Error getting all places: " + e.getMessage());
            throw new RemoteException("Failed to get places: " + e.getMessage());
        }
    }
    
    @Override
    public List<Place> getPlacesByStatus(PlaceStatus status) throws RemoteException {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT * FROM places WHERE status = ? ORDER BY created_at DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                places.add(extractPlaceFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + places.size() + " places with status: " + status);
            return places;
            
        } catch (SQLException e) {
            System.err.println("Error getting places by status: " + e.getMessage());
            throw new RemoteException("Failed to get places: " + e.getMessage());
        }
    }
    
    @Override
    public Place getPlaceById(int placeId) throws RemoteException {
        String sql = "SELECT * FROM places WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, placeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPlaceFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error getting place by ID: " + e.getMessage());
            throw new RemoteException("Failed to get place: " + e.getMessage());
        }
    }
    
    @Override
    public int addPlace(Place place) throws RemoteException {
        String sql = "INSERT INTO places (name, description, category, address, location_lat, location_lng, " +
                    "rating_wifi, rating_power, rating_service, rating_overall, status, contributed_by, " +
                    "image_url, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, place.getName());
            stmt.setString(2, place.getDescription());
            stmt.setString(3, place.getCategory());
            stmt.setString(4, place.getAddress());
            stmt.setDouble(5, place.getLocationLat());
            stmt.setDouble(6, place.getLocationLng());
            stmt.setDouble(7, place.getRatingWifi());
            stmt.setDouble(8, place.getRatingPower());
            stmt.setDouble(9, place.getRatingService());
            stmt.setDouble(10, place.getRatingOverall());
            stmt.setString(11, place.getStatus().getValue());
            stmt.setObject(12, place.getContributedBy());
            stmt.setString(13, place.getImageUrl());
            stmt.setTimestamp(14, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int placeId = generatedKeys.getInt(1);
                    System.out.println("✓ Place added: " + place.getName() + " (ID: " + placeId + ")");
                    return placeId;
                }
            }
            
            throw new RemoteException("Failed to add place");
            
        } catch (SQLException e) {
            System.err.println("Error adding place: " + e.getMessage());
            throw new RemoteException("Failed to add place: " + e.getMessage());
        }
    }
    
    @Override
    public boolean updatePlace(Place place) throws RemoteException {
        String sql = "UPDATE places SET name = ?, description = ?, category = ?, address = ?, " +
                    "location_lat = ?, location_lng = ?, rating_wifi = ?, rating_power = ?, " +
                    "rating_service = ?, rating_overall = ?, status = ?, image_url = ?, " +
                    "updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, place.getName());
            stmt.setString(2, place.getDescription());
            stmt.setString(3, place.getCategory());
            stmt.setString(4, place.getAddress());
            stmt.setDouble(5, place.getLocationLat());
            stmt.setDouble(6, place.getLocationLng());
            stmt.setDouble(7, place.getRatingWifi());
            stmt.setDouble(8, place.getRatingPower());
            stmt.setDouble(9, place.getRatingService());
            stmt.setDouble(10, place.getRatingOverall());
            stmt.setString(11, place.getStatus().getValue());
            stmt.setString(12, place.getImageUrl());
            stmt.setTimestamp(13, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(14, place.getId());
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ Place updated: " + place.getName());
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating place: " + e.getMessage());
            throw new RemoteException("Failed to update place: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deletePlace(int placeId) throws RemoteException {
        String sql = "DELETE FROM places WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, placeId);
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ Place deleted: ID " + placeId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting place: " + e.getMessage());
            throw new RemoteException("Failed to delete place: " + e.getMessage());
        }
    }
    
    @Override
    public boolean approvePlace(int placeId) throws RemoteException {
        return updatePlaceStatus(placeId, PlaceStatus.APPROVED);
    }
    
    @Override
    public boolean rejectPlace(int placeId) throws RemoteException {
        return updatePlaceStatus(placeId, PlaceStatus.REJECTED);
    }
    
    private boolean updatePlaceStatus(int placeId, PlaceStatus status) throws RemoteException {
        String sql = "UPDATE places SET status = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, placeId);
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ Place status updated to " + status + ": ID " + placeId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating place status: " + e.getMessage());
            throw new RemoteException("Failed to update place status: " + e.getMessage());
        }
    }
    
    @Override
    public List<Place> searchPlaces(String query) throws RemoteException {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT * FROM places WHERE status = 'approved' AND " +
                    "(name LIKE ? OR description LIKE ? OR category LIKE ? OR address LIKE ?) " +
                    "ORDER BY rating_overall DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + query + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                places.add(extractPlaceFromResultSet(rs));
            }
            
            System.out.println("✓ Search found " + places.size() + " places for query: " + query);
            return places;
            
        } catch (SQLException e) {
            System.err.println("Error searching places: " + e.getMessage());
            throw new RemoteException("Failed to search places: " + e.getMessage());
        }
    }
    
    @Override
    public List<Place> getPlacesByCategory(String category) throws RemoteException {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT * FROM places WHERE status = 'approved' AND category = ? " +
                    "ORDER BY rating_overall DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                places.add(extractPlaceFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + places.size() + " places in category: " + category);
            return places;
            
        } catch (SQLException e) {
            System.err.println("Error getting places by category: " + e.getMessage());
            throw new RemoteException("Failed to get places: " + e.getMessage());
        }
    }
    
    @Override
    public List<Place> getUserFavorites(int userId) throws RemoteException {
        List<Place> places = new ArrayList<>();
        String sql = "SELECT p.* FROM places p " +
                    "INNER JOIN favorites f ON p.id = f.place_id " +
                    "WHERE f.user_id = ? ORDER BY f.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                places.add(extractPlaceFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + places.size() + " favorites for user ID: " + userId);
            return places;
            
        } catch (SQLException e) {
            System.err.println("Error getting user favorites: " + e.getMessage());
            throw new RemoteException("Failed to get favorites: " + e.getMessage());
        }
    }
    
    @Override
    public boolean addToFavorites(int userId, int placeId) throws RemoteException {
        String sql = "INSERT INTO favorites (user_id, place_id, created_at) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, placeId);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ Added to favorites: User " + userId + ", Place " + placeId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) { // Duplicate entry
                System.out.println("Place already in favorites");
                return false;
            }
            System.err.println("Error adding to favorites: " + e.getMessage());
            throw new RemoteException("Failed to add to favorites: " + e.getMessage());
        }
    }
    
    @Override
    public boolean removeFromFavorites(int userId, int placeId) throws RemoteException {
        String sql = "DELETE FROM favorites WHERE user_id = ? AND place_id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, placeId);
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ Removed from favorites: User " + userId + ", Place " + placeId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error removing from favorites: " + e.getMessage());
            throw new RemoteException("Failed to remove from favorites: " + e.getMessage());
        }
    }
    
    @Override
    public boolean isFavorite(int userId, int placeId) throws RemoteException {
        String sql = "SELECT COUNT(*) FROM favorites WHERE user_id = ? AND place_id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, placeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Error checking favorite: " + e.getMessage());
            throw new RemoteException("Failed to check favorite: " + e.getMessage());
        }
    }
    
    @Override
    public int getPlaceCount() throws RemoteException {
        String sql = "SELECT COUNT(*) FROM places";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("Error getting place count: " + e.getMessage());
            throw new RemoteException("Failed to get place count: " + e.getMessage());
        }
    }
    
    @Override
    public int getPendingPlaceCount() throws RemoteException {
        String sql = "SELECT COUNT(*) FROM places WHERE status = 'pending'";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("Error getting pending place count: " + e.getMessage());
            throw new RemoteException("Failed to get pending place count: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to extract Place object from ResultSet
     */
    private Place extractPlaceFromResultSet(ResultSet rs) throws SQLException {
        Place place = new Place();
        place.setId(rs.getInt("id"));
        place.setName(rs.getString("name"));
        place.setDescription(rs.getString("description"));
        place.setCategory(rs.getString("category"));
        place.setAddress(rs.getString("address"));
        place.setLocationLat(rs.getDouble("location_lat"));
        place.setLocationLng(rs.getDouble("location_lng"));
        place.setRatingWifi(rs.getDouble("rating_wifi"));
        place.setRatingPower(rs.getDouble("rating_power"));
        place.setRatingService(rs.getDouble("rating_service"));
        place.setRatingOverall(rs.getDouble("rating_overall"));
        place.setStatus(PlaceStatus.fromString(rs.getString("status")));
        
        Integer contributedBy = (Integer) rs.getObject("contributed_by");
        place.setContributedBy(contributedBy);
        
        place.setImageUrl(rs.getString("image_url"));
        place.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            place.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        return place;
    }
}
