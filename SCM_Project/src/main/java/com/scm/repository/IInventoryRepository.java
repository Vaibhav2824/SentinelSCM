package com.scm.repository;

import com.scm.model.InventoryItem;
import java.util.List;

/** IInventoryRepository - interface for inventory data access. */
public interface IInventoryRepository {
    List<InventoryItem> findAll();
    InventoryItem findById(int itemId);
    void updateQuantity(int itemId, int quantity);
}
