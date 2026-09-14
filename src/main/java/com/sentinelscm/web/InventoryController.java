package com.sentinelscm.web;

import com.sentinelscm.domain.InventoryItem;
import com.sentinelscm.security.Access;
import com.sentinelscm.service.InventoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventory;

    public InventoryController(InventoryService inventory) {
        this.inventory = inventory;
    }

    @GetMapping
    @PreAuthorize(Access.ADMIN_PM_WM)
    public String list(Model model) {
        model.addAttribute("items", inventory.list());
        model.addAttribute("lowStock", inventory.lowStockCount());
        model.addAttribute("threshold", InventoryItem.LOW_STOCK_THRESHOLD);
        return "inventory";
    }

    @PostMapping("/{id}")
    @PreAuthorize(Access.ADMIN_WM)
    public String update(@PathVariable Integer id, @RequestParam int quantity, RedirectAttributes flash) {
        try {
            InventoryItem item = inventory.updateStock(id, quantity);
            flash.addFlashAttribute("success", item.getItemName() + " set to " + item.getQuantity() + " units.");
        } catch (IllegalArgumentException e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventory";
    }
}
