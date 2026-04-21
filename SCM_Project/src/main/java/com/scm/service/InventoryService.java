package com.scm.service;

import com.scm.model.InventoryItem;
import com.scm.repository.IInventoryRepository;
import java.util.List;

/**
 * InventoryService - business logic for inventory management.
 *
 * SOLID: SRP - only handles inventory operations
 * GRASP: High Cohesion - focused on inventory
 * GRASP: Low Coupling - depends on IInventoryRepository (interface)
 */
public class InventoryService {

    private final IInventoryRepository inventoryRepo;

    public InventoryService(IInventoryRepository inventoryRepo) {
        this.inventoryRepo = inventoryRepo;
    }

    public List<InventoryItem> getAllItems() { return inventoryRepo.findAll(); }

    public InventoryItem getItemById(int id) { return inventoryRepo.findById(id); }

    public void updateStock(int itemId, int quantity) {
        if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
        inventoryRepo.updateQuantity(itemId, quantity);
    }
}
