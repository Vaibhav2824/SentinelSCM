package com.scm.repository;

import com.scm.db.DatabaseManager;
import com.scm.model.InventoryItem;
import java.sql.*;
import java.util.*;

/** MySQLInventoryRepository - MySQL implementation of IInventoryRepository. */
public class MySQLInventoryRepository implements IInventoryRepository {

    private Connection conn() { return DatabaseManager.getInstance().getConnection(); }

    @Override
    public List<InventoryItem> findAll() {
        List<InventoryItem> list = new ArrayList<>();
        try (Statement st = conn().createStatement();
             ResultSet rs = st.executeQuery("SELECT * FROM inventory ORDER BY item_id")) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) { throw new RuntimeException("findAll inventory failed: " + e.getMessage(), e); }
        return list;
    }

    @Override
    public InventoryItem findById(int itemId) {
        try (PreparedStatement ps = conn().prepareStatement("SELECT * FROM inventory WHERE item_id=?")) {
            ps.setInt(1, itemId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapRow(rs);
        } catch (SQLException e) { throw new RuntimeException("findById inventory failed: " + e.getMessage(), e); }
        return null;
    }

    @Override
    public void updateQuantity(int itemId, int quantity) {
        try (PreparedStatement ps = conn().prepareStatement("UPDATE inventory SET quantity=? WHERE item_id=?")) {
            ps.setInt(1, quantity); ps.setInt(2, itemId); ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException("updateQuantity failed: " + e.getMessage(), e); }
    }

    private InventoryItem mapRow(ResultSet rs) throws SQLException {
        return new InventoryItem(rs.getInt("item_id"), rs.getString("item_name"),
            rs.getInt("quantity"), rs.getString("last_updated"));
    }
}
