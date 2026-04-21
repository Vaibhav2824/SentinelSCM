package com.scm.repository;

import com.scm.db.DatabaseManager;
import com.scm.model.EvaluationCriteria;
import com.scm.model.RiskScore;
import java.sql.*;
import java.util.*;

/** MySQLRiskRepository - MySQL implementation of IRiskRepository. */
public class MySQLRiskRepository implements IRiskRepository {

    private Connection conn() { return DatabaseManager.getInstance().getConnection(); }

    @Override
    public EvaluationCriteria findLatestCriteria(int vendorId) {
        String sql = "SELECT * FROM evaluation_criteria WHERE vendor_id=? ORDER BY criteria_id DESC LIMIT 1";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapCriteria(rs);
        } catch (SQLException e) { throw new RuntimeException("findLatestCriteria failed: " + e.getMessage(), e); }
        return null;
    }

    @Override
    public List<EvaluationCriteria> findCriteriaHistory(int vendorId) {
        List<EvaluationCriteria> list = new ArrayList<>();
        String sql = "SELECT * FROM evaluation_criteria WHERE vendor_id=? ORDER BY criteria_id DESC LIMIT 10";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapCriteria(rs));
        } catch (SQLException e) { throw new RuntimeException("findCriteriaHistory failed: " + e.getMessage(), e); }
        return list;
    }

    @Override
    public void saveCriteria(EvaluationCriteria ec) {
        String sql = "INSERT INTO evaluation_criteria (vendor_id, delivery_timeliness, defect_rate, compliance_score, evaluated_date) VALUES (?, ?, ?, ?, CURDATE())";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, ec.getVendorId()); ps.setDouble(2, ec.getDeliveryTimeliness());
            ps.setDouble(3, ec.getDefectRate()); ps.setDouble(4, ec.getComplianceScore());
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("saveCriteria failed: " + e.getMessage(), e); }
    }

    @Override
    public void saveRiskScore(RiskScore rs) {
        String sql = "INSERT INTO risk_scores (vendor_id, score) VALUES (?, ?)";
        try (PreparedStatement ps = conn().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, rs.getVendorId()); ps.setDouble(2, rs.getScore());
            ps.executeUpdate();
            ResultSet keys = ps.getGeneratedKeys();
            if (keys.next()) rs.setScoreId(keys.getInt(1));
        } catch (SQLException e) { throw new RuntimeException("saveRiskScore failed: " + e.getMessage(), e); }
    }

    @Override
    public List<RiskScore> findRiskHistory(int vendorId) {
        List<RiskScore> list = new ArrayList<>();
        String sql = "SELECT * FROM risk_scores WHERE vendor_id=? ORDER BY calculated_date ASC LIMIT 20";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(new RiskScore(rs.getInt("score_id"), vendorId, rs.getDouble("score"), rs.getString("calculated_date")));
        } catch (SQLException e) { throw new RuntimeException("findRiskHistory failed: " + e.getMessage(), e); }
        return list;
    }

    @Override
    public RiskScore findLatestRiskScore(int vendorId) {
        String sql = "SELECT * FROM risk_scores WHERE vendor_id=? ORDER BY score_id DESC LIMIT 1";
        try (PreparedStatement ps = conn().prepareStatement(sql)) {
            ps.setInt(1, vendorId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return new RiskScore(rs.getInt("score_id"), vendorId, rs.getDouble("score"), rs.getString("calculated_date"));
        } catch (SQLException e) { throw new RuntimeException("findLatestRiskScore failed: " + e.getMessage(), e); }
        return null;
    }

    @Override
    public double getThreshold() {
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT threshold FROM risk_rules ORDER BY rule_id DESC LIMIT 1")) {
            if (rs.next()) return rs.getDouble("threshold");
        } catch (SQLException e) { /* fallback */ }
        return 0.7;
    }

    private EvaluationCriteria mapCriteria(ResultSet rs) throws SQLException {
        return new EvaluationCriteria(
            rs.getInt("criteria_id"), rs.getInt("vendor_id"),
            rs.getDouble("delivery_timeliness"), rs.getDouble("defect_rate"),
            rs.getDouble("compliance_score"), rs.getString("evaluated_date")
        );
    }
}
