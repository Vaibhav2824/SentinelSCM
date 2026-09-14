package com.sentinelscm.web;

import com.sentinelscm.domain.Vendor;
import com.sentinelscm.security.Access;
import com.sentinelscm.service.VendorCommand;
import com.sentinelscm.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vendors")
public class VendorController {

    private final VendorService vendors;

    public VendorController(VendorService vendors) {
        this.vendors = vendors;
    }

    @GetMapping
    @PreAuthorize(Access.ANY_USER)
    public String list(Model model) {
        model.addAttribute("vendors", vendors.listVisible());
        return "vendors";
    }

    @GetMapping("/new")
    @PreAuthorize(Access.ADMIN_PM)
    public String createForm(Model model) {
        model.addAttribute("form", new VendorCommand("", "", 3.0));
        model.addAttribute("vendorId", null);
        return "vendor-form";
    }

    @PostMapping
    @PreAuthorize(Access.ADMIN_PM)
    public String create(@Valid @ModelAttribute("form") VendorCommand form, BindingResult binding,
                         Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            model.addAttribute("vendorId", null);
            return "vendor-form";
        }
        Vendor created = vendors.create(form);
        flash.addFlashAttribute("success", "Vendor \"" + created.getName() + "\" created.");
        return "redirect:/vendors";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize(Access.ADMIN_PM)
    public String editForm(@PathVariable Integer id, Model model) {
        Vendor v = vendors.get(id);
        model.addAttribute("form", new VendorCommand(v.getName(), v.getContact(), v.getRating()));
        model.addAttribute("vendorId", id);
        return "vendor-form";
    }

    @PostMapping("/{id}")
    @PreAuthorize(Access.ADMIN_PM)
    public String update(@PathVariable Integer id, @Valid @ModelAttribute("form") VendorCommand form,
                         BindingResult binding, Model model, RedirectAttributes flash) {
        if (binding.hasErrors()) {
            model.addAttribute("vendorId", id);
            return "vendor-form";
        }
        vendors.update(id, form);
        flash.addFlashAttribute("success", "Vendor updated.");
        return "redirect:/vendors";
    }

    @PostMapping("/{id}/suspend")
    @PreAuthorize(Access.ADMIN_PM)
    public String suspend(@PathVariable Integer id, RedirectAttributes flash) {
        vendors.suspend(id);
        flash.addFlashAttribute("warning", "Vendor suspended.");
        return "redirect:/vendors";
    }

    @PostMapping("/{id}/activate")
    @PreAuthorize(Access.ADMIN_PM)
    public String activate(@PathVariable Integer id, RedirectAttributes flash) {
        vendors.activate(id);
        flash.addFlashAttribute("success", "Vendor reactivated.");
        return "redirect:/vendors";
    }

    @PostMapping("/{id}/blacklist")
    @PreAuthorize(Access.ADMIN)
    public String blacklist(@PathVariable Integer id, RedirectAttributes flash) {
        vendors.blacklist(id);
        flash.addFlashAttribute("error", "Vendor blacklisted.");
        return "redirect:/vendors";
    }

    @PostMapping("/{id}/delete")
    @PreAuthorize(Access.ADMIN)
    public String delete(@PathVariable Integer id, RedirectAttributes flash) {
        vendors.softDelete(id);
        flash.addFlashAttribute("warning", "Vendor deactivated (soft-deleted).");
        return "redirect:/vendors";
    }
}
