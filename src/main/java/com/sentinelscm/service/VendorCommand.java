package com.sentinelscm.service;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Create/update payload for a vendor. Validated at the web boundary and re-checked in the service. */
public record VendorCommand(
    @NotBlank @Size(max = 150) String name,
    @Size(max = 200) String contact,
    @DecimalMin("0.0") @DecimalMax("5.0") double rating
) { }
