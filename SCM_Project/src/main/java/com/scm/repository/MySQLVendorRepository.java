package com.scm.repository;

import com.scm.db.DatabaseManager;
import com.scm.model.Vendor;
import java.sql.*;
import java.util.*;

/**
 * MySQLVendorRepository - MySQL implementation of IVendorRepository.
 *
 * SOLID: DIP - implements IVendorRepository interface
 * GRASP: Indirection - sits between VendorService and raw JDBC
 * GRASP: Protected Variations - swap implementation without touching VendorService
 */
public class MySQLVendorRepository implements IVendorRepository {

    private Connection conn() { return DatabaseManager.getInstance().getConnection(); }

    @Override
    public Vendor findById(int vendorId) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM vendors WHERE vendor_id = ?")) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { throw new RuntimeException("findById failed: " + e.getMessage(), e); }
        return null;
    }

    @Override
    public List<Vendor> findAll() {
        List<Vendor> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM vendors WHERE status != 'INACTIVE' ORDER BY vendor_id")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException("findAll failed: " + e.getMessage(), e); }
        return list;
    }

    @Override
    public List<Vendor> findByStatus(String status) {
        List<Vendor> list = new ArrayList<>();
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM vendors WHERE status = ? ORDER BY vendor_id")) {
            ps.setString(1, status);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException("findByStatus failed: " + e.getMessage(), e); }
        return list;
    }

    @Override
    public int save(Vendor vendor) {
        String sql = "INSERT INTO vendors (name, contact, rating, risk_score, status, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, vendor.getName()); ps.setString(2, vendor.getContact());
            ps.setDouble(3, vendor.getRating()); ps.setDouble(4, vendor.getRiskScore());
            ps.setString(5, vendor.getStatus() != null ? vendor.getStatus() : "PENDING");
            if (vendor.getUserId() != null) ps.setInt(6, vendor.getUserId());
            else ps.setNull(6, Types.INTEGER);
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) { throw new RuntimeException("save vendor failed: " + e.getMessage(), e); }
        return -1;
    }

    @Override
    public void update(Vendor vendor) {
        String sql = "UPDATE vendors SET name=?, contact=?, rating=?, risk_score=?, status=? WHERE vendor_id=?";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setString(1, vendor.getName()); ps.setString(2, vendor.getContact());
            ps.setDouble(3, vendor.getRating()); ps.setDouble(4, vendor.getRiskScore());
            ps.setString(5, vendor.getStatus()); ps.setInt(6, vendor.getVendorId());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("update vendor failed: " + e.getMessage(), e); }
    }

    @Override
    public void softDelete(int vendorId) { updateStatus(vendorId, "INACTIVE"); }

    @Override
    public void updateStatus(int vendorId, String status) {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE vendors SET status=? WHERE vendor_id=?")) {
            ps.setString(1, status); ps.setInt(2, vendorId); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("updateStatus failed: " + e.getMessage(), e); }
    }

    @Override
    public void updateRiskScore(int vendorId, double riskScore) {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE vendors SET risk_score=? WHERE vendor_id=?")) {
            ps.setDouble(1, riskScore); ps.setInt(2, vendorId); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("updateRiskScore failed: " + e.getMessage(), e); }
    }

    private Vendor mapRow(ResultSet rs) throws SQLException {
        return new Vendor(
            rs.getInt("vendor_id"), rs.getString("name"), rs.getString("contact"),
            rs.getDouble("rating"), rs.getDouble("risk_score"), rs.getString("status"),
            rs.getObject("user_id") != null ? rs.getInt("user_id") : null
        );
    }
}
