package com.sentinelscm.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record ThresholdRequest(
    @NotNull @DecimalMin(value = "0.0", inclusive = false) @DecimalMax(value = "1.0", inclusive = false) Double threshold
) { }
