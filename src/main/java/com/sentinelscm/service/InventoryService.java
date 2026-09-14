package com.sentinelscm.service;

import com.sentinelscm.domain.InventoryItem;
import com.sentinelscm.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class InventoryService {

    private final InventoryItemRepository items;

    public InventoryService(InventoryItemRepository items) {
        this.items = items;
    }

    @Transactional(readOnly = true)
    public List<InventoryItem> list() { return items.findAllByOrderByIdAsc(); }

    @Transactional(readOnly = true)
    public long lowStockCount() { return items.countByQuantityLessThan(InventoryItem.LOW_STOCK_THRESHOLD); }

    public InventoryItem updateStock(Integer id, int quantity) {
        InventoryItem item = items.findById(id).orElseThrow(() -> new NotFoundException("Inventory item", id));
        item.updateQuantity(quantity);
        return item;
    }
}
