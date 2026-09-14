package com.sentinelscm.api;

import com.sentinelscm.api.dto.VendorDto;
import com.sentinelscm.security.Access;
import com.sentinelscm.service.VendorCommand;
import com.sentinelscm.service.VendorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendor")
public class VendorApiController {

    private final VendorService vendors;

    public VendorApiController(VendorService vendors) {
        this.vendors = vendors;
    }

    @GetMapping
    @PreAuthorize(Access.ANY_USER)
    public List<VendorDto> list() {
        return vendors.listVisible().stream().map(VendorDto::from).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize(Access.ADMIN_PM_RA_VENDOR)
    public VendorDto get(@PathVariable Integer id) {
        return VendorDto.from(vendors.get(id));
    }

    @PostMapping
    @PreAuthorize(Access.ADMIN_PM)
    @ResponseStatus(HttpStatus.CREATED)
    public VendorDto create(@Valid @RequestBody VendorCommand cmd) {
        return VendorDto.from(vendors.create(cmd));
    }

    @PutMapping("/{id}")
    @PreAuthorize(Access.ADMIN_PM)
    public VendorDto update(@PathVariable Integer id, @Valid @RequestBody VendorCommand cmd) {
        return VendorDto.from(vendors.update(id, cmd));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize(Access.ADMIN)
    public Map<String, Object> delete(@PathVariable Integer id) {
        vendors.softDelete(id);
        return Map.of("success", true, "message", "Vendor deactivated");
    }

    @PutMapping("/{id}/suspend")
    @PreAuthorize(Access.ADMIN_PM)
    public VendorDto suspend(@PathVariable Integer id) {
        vendors.suspend(id);
        return VendorDto.from(vendors.get(id));
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize(Access.ADMIN_PM)
    public VendorDto activate(@PathVariable Integer id) {
        vendors.activate(id);
        return VendorDto.from(vendors.get(id));
    }

    @PutMapping("/{id}/blacklist")
    @PreAuthorize(Access.ADMIN)
    public VendorDto blacklist(@PathVariable Integer id) {
        vendors.blacklist(id);
        return VendorDto.from(vendors.get(id));
    }
}
