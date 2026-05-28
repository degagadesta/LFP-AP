package com.lfp.rmi;

import com.lfp.model.Report;
import com.lfp.model.ReportStatus;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

/**
 * RMI Service interface for Report operations
 * Remote interface for distributed report management
 */
public interface ReportService extends Remote {
    
    /**
     * Get all reports
     * @return List of all reports
     * @throws RemoteException if RMI communication fails
     */
    List<Report> getAllReports() throws RemoteException;
    
    /**
     * Get reports by status
     * @param status Report status (PENDING, RESOLVED, REJECTED)
     * @return List of reports with specified status
     * @throws RemoteException if RMI communication fails
     */
    List<Report> getReportsByStatus(ReportStatus status) throws RemoteException;
    
    /**
     * Get report by ID
     * @param reportId Report ID
     * @return Report object or null if not found
     * @throws RemoteException if RMI communication fails
     */
    Report getReportById(int reportId) throws RemoteException;
    
    /**
     * Get reports for a specific place
     * @param placeId Place ID
     * @return List of reports for the place
     * @throws RemoteException if RMI communication fails
     */
    List<Report> getReportsByPlace(int placeId) throws RemoteException;
    
    /**
     * Get reports submitted by a user
     * @param userId User ID
     * @return List of reports submitted by the user
     * @throws RemoteException if RMI communication fails
     */
    List<Report> getReportsByUser(int userId) throws RemoteException;
    
    /**
     * Create a new report
     * @param report Report object to create
     * @return ID of newly created report
     * @throws RemoteException if RMI communication fails
     */
    int createReport(Report report) throws RemoteException;
    
    /**
     * Update report status (admin only)
     * @param reportId Report ID
     * @param status New status
     * @param adminNotes Admin notes
     * @return true if update successful
     * @throws RemoteException if RMI communication fails
     */
    boolean updateReportStatus(int reportId, ReportStatus status, String adminNotes) throws RemoteException;
    
    /**
     * Delete a report
     * @param reportId Report ID to delete
     * @return true if deletion successful
     * @throws RemoteException if RMI communication fails
     */
    boolean deleteReport(int reportId) throws RemoteException;
    
    /**
     * Get total number of reports
     * @return Total report count
     * @throws RemoteException if RMI communication fails
     */
    int getReportCount() throws RemoteException;
    
    /**
     * Get number of pending reports
     * @return Pending report count
     * @throws RemoteException if RMI communication fails
     */
    int getPendingReportCount() throws RemoteException;
}
