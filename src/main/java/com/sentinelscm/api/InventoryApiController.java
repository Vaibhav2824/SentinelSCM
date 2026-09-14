package com.sentinelscm.api;

import com.sentinelscm.api.dto.StockUpdateRequest;
import com.sentinelscm.domain.InventoryItem;
import com.sentinelscm.security.Access;
import com.sentinelscm.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryApiController {

    private final InventoryService inventory;

    public InventoryApiController(InventoryService inventory) {
        this.inventory = inventory;
    }

    @GetMapping
    @PreAuthorize(Access.ADMIN_PM_WM)
    public List<InventoryItem> list() {
        return inventory.list();
    }

    @PutMapping("/{id}")
    @PreAuthorize(Access.ADMIN_WM)
    public InventoryItem updateStock(@PathVariable Integer id, @Valid @RequestBody StockUpdateRequest req) {
        return inventory.updateStock(id, req.quantity());
    }
}
