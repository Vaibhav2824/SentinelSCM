package com.scm.repository;

import com.scm.db.DatabaseManager;
import com.scm.model.Alert;
import com.scm.model.Recommendation;
import java.sql.*;
import java.util.*;

/** MySQLAlertRepository - MySQL implementation of IAlertRepository. */
public class MySQLAlertRepository implements IAlertRepository {

    private Connection conn() { return DatabaseManager.getInstance().getConnection(); }

    @Override
    public List<Alert> findAll() {
        List<Alert> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM alerts ORDER BY timestamp DESC")) {
            while (rs.next()) list.add(mapAlert(rs));
        } catch (SQLException e) { throw new RuntimeException("findAll alerts failed: " + e.getMessage(), e); }
        return list;
    }

    @Override
    public List<Alert> findUnresolved() {
        List<Alert> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM alerts WHERE resolved=0 ORDER BY timestamp DESC")) {
            while (rs.next()) list.add(mapAlert(rs));
        } catch (SQLException e) { throw new RuntimeException("findUnresolved alerts failed: " + e.getMessage(), e); }
        return list;
    }

    @Override
    public Alert findById(int alertId) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM alerts WHERE alert_id=?")) {
            ps.setInt(1, alertId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapAlert(rs);
        } catch (SQLException e) { throw new RuntimeException("findById alert failed: " + e.getMessage(), e); }
        return null;
    }

    @Override
    public int saveAlert(Alert alert) {
        String sql = "INSERT INTO alerts (vendor_id, message, severity, resolved) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, alert.getVendorId()); ps.setString(2, alert.getMessage());
            ps.setString(3, alert.getSeverity()); ps.setInt(4, alert.isResolved() ? 1 : 0);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) { throw new RuntimeException("saveAlert failed: " + e.getMessage(), e); }
        return -1;
    }

    @Override
    public void resolveAlert(int alertId) {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE alerts SET resolved=1 WHERE alert_id=?")) {
            ps.setInt(1, alertId); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("resolveAlert failed: " + e.getMessage(), e); }
    }

    @Override
    public List<Recommendation> findRecommendationsByAlertId(int alertId) {
        List<Recommendation> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM recommendations WHERE alert_id=?")) {
            ps.setInt(1, alertId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new Recommendation(rs.getInt("rec_id"), alertId, rs.getInt("suggested_vendor_id"), rs.getString("reason")));
        } catch (SQLException e) { throw new RuntimeException("findRecommendations failed: " + e.getMessage(), e); }
        return list;
    }

    @Override
    public void saveRecommendation(Recommendation rec) {
        String sql = "INSERT INTO recommendations (alert_id, suggested_vendor_id, reason) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, rec.getAlertId()); ps.setInt(2, rec.getSuggestedVendorId());
            ps.setString(3, rec.getReason()); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("saveRecommendation failed: " + e.getMessage(), e); }
    }

    private Alert mapAlert(ResultSet rs) throws SQLException {
        return new Alert(
            rs.getInt("alert_id"), rs.getInt("vendor_id"), rs.getString("message"),
            rs.getString("timestamp"), rs.getString("severity"), rs.getInt("resolved") == 1
        );
    }
}
