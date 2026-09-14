package com.sentinelscm.api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record EvaluationRequest(
    @NotNull @DecimalMin("0.0") @DecimalMax("1.0") Double deliveryTimeliness,
    @NotNull @DecimalMin("0.0") @DecimalMax("1.0") Double defectRate,
    @NotNull @DecimalMin("0.0") @DecimalMax("1.0") Double complianceScore
) { }
