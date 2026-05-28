package com.lfp.rmi;

import com.lfp.model.Report;
import com.lfp.model.ReportStatus;
import com.lfp.util.DatabaseUtil;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of ReportService RMI interface
 * Handles all report-related database operations
 */
public class ReportServiceImpl extends UnicastRemoteObject implements ReportService {
    private static final long serialVersionUID = 1L;
    
    public ReportServiceImpl() throws RemoteException {
        super();
    }
    
    @Override
    public List<Report> getAllReports() throws RemoteException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT r.*, p.name as place_name, u.username as reported_by " +
                    "FROM reports r " +
                    "INNER JOIN places p ON r.place_id = p.id " +
                    "INNER JOIN users u ON r.user_id = u.id " +
                    "ORDER BY r.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                reports.add(extractReportFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + reports.size() + " reports");
            return reports;
            
        } catch (SQLException e) {
            System.err.println("Error getting all reports: " + e.getMessage());
            throw new RemoteException("Failed to get reports: " + e.getMessage());
        }
    }
    
    @Override
    public List<Report> getReportsByStatus(ReportStatus status) throws RemoteException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT r.*, p.name as place_name, u.username as reported_by " +
                    "FROM reports r " +
                    "INNER JOIN places p ON r.place_id = p.id " +
                    "INNER JOIN users u ON r.user_id = u.id " +
                    "WHERE r.status = ? " +
                    "ORDER BY r.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reports.add(extractReportFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + reports.size() + " reports with status: " + status);
            return reports;
            
        } catch (SQLException e) {
            System.err.println("Error getting reports by status: " + e.getMessage());
            throw new RemoteException("Failed to get reports: " + e.getMessage());
        }
    }
    
    @Override
    public Report getReportById(int reportId) throws RemoteException {
        String sql = "SELECT r.*, p.name as place_name, u.username as reported_by " +
                    "FROM reports r " +
                    "INNER JOIN places p ON r.place_id = p.id " +
                    "INNER JOIN users u ON r.user_id = u.id " +
                    "WHERE r.id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reportId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractReportFromResultSet(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("Error getting report by ID: " + e.getMessage());
            throw new RemoteException("Failed to get report: " + e.getMessage());
        }
    }
    
    @Override
    public List<Report> getReportsByPlace(int placeId) throws RemoteException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT r.*, p.name as place_name, u.username as reported_by " +
                    "FROM reports r " +
                    "INNER JOIN places p ON r.place_id = p.id " +
                    "INNER JOIN users u ON r.user_id = u.id " +
                    "WHERE r.place_id = ? " +
                    "ORDER BY r.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, placeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reports.add(extractReportFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + reports.size() + " reports for place ID: " + placeId);
            return reports;
            
        } catch (SQLException e) {
            System.err.println("Error getting reports by place: " + e.getMessage());
            throw new RemoteException("Failed to get reports: " + e.getMessage());
        }
    }
    
    @Override
    public List<Report> getReportsByUser(int userId) throws RemoteException {
        List<Report> reports = new ArrayList<>();
        String sql = "SELECT r.*, p.name as place_name, u.username as reported_by " +
                    "FROM reports r " +
                    "INNER JOIN places p ON r.place_id = p.id " +
                    "INNER JOIN users u ON r.user_id = u.id " +
                    "WHERE r.user_id = ? " +
                    "ORDER BY r.created_at DESC";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                reports.add(extractReportFromResultSet(rs));
            }
            
            System.out.println("✓ Retrieved " + reports.size() + " reports by user ID: " + userId);
            return reports;
            
        } catch (SQLException e) {
            System.err.println("Error getting reports by user: " + e.getMessage());
            throw new RemoteException("Failed to get reports: " + e.getMessage());
        }
    }
    
    @Override
    public int createReport(Report report) throws RemoteException {
        String sql = "INSERT INTO reports (place_id, user_id, reason, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, report.getPlaceId());
            stmt.setInt(2, report.getUserId());
            stmt.setString(3, report.getReason());
            stmt.setString(4, report.getStatus().getValue());
            stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int reportId = generatedKeys.getInt(1);
                    System.out.println("✓ Report created: ID " + reportId);
                    return reportId;
                }
            }
            
            throw new RemoteException("Failed to create report");
            
        } catch (SQLException e) {
            System.err.println("Error creating report: " + e.getMessage());
            throw new RemoteException("Failed to create report: " + e.getMessage());
        }
    }
    
    @Override
    public boolean updateReportStatus(int reportId, ReportStatus status, String adminNotes) throws RemoteException {
        String sql = "UPDATE reports SET status = ?, admin_notes = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status.getValue());
            stmt.setString(2, adminNotes);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(4, reportId);
            
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ Report status updated to " + status + ": ID " + reportId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating report status: " + e.getMessage());
            throw new RemoteException("Failed to update report status: " + e.getMessage());
        }
    }
    
    @Override
    public boolean deleteReport(int reportId) throws RemoteException {
        String sql = "DELETE FROM reports WHERE id = ?";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, reportId);
            int affectedRows = stmt.executeUpdate();
            System.out.println("✓ Report deleted: ID " + reportId);
            return affectedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting report: " + e.getMessage());
            throw new RemoteException("Failed to delete report: " + e.getMessage());
        }
    }
    
    @Override
    public int getReportCount() throws RemoteException {
        String sql = "SELECT COUNT(*) FROM reports";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("Error getting report count: " + e.getMessage());
            throw new RemoteException("Failed to get report count: " + e.getMessage());
        }
    }
    
    @Override
    public int getPendingReportCount() throws RemoteException {
        String sql = "SELECT COUNT(*) FROM reports WHERE status = 'pending'";
        
        try (Connection conn = DatabaseUtil.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (SQLException e) {
            System.err.println("Error getting pending report count: " + e.getMessage());
            throw new RemoteException("Failed to get pending report count: " + e.getMessage());
        }
    }
    
    /**
     * Helper method to extract Report object from ResultSet
     */
    private Report extractReportFromResultSet(ResultSet rs) throws SQLException {
        Report report = new Report();
        report.setId(rs.getInt("id"));
        report.setPlaceId(rs.getInt("place_id"));
        report.setUserId(rs.getInt("user_id"));
        report.setReason(rs.getString("reason"));
        report.setStatus(ReportStatus.fromString(rs.getString("status")));
        report.setAdminNotes(rs.getString("admin_notes"));
        report.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            report.setUpdatedAt(updatedAt.toLocalDateTime());
        }
        
        // Additional fields from JOIN
        report.setPlaceName(rs.getString("place_name"));
        report.setReportedBy(rs.getString("reported_by"));
        
        return report;
    }
}
