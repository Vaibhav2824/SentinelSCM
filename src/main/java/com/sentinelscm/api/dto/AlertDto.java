package com.sentinelscm.api.dto;

import com.sentinelscm.domain.Alert;

import java.time.LocalDateTime;
import java.util.function.Function;

public record AlertDto(
    Integer alertId,
    Integer vendorId,
    String vendorName,
    String message,
    String severity,
    boolean resolved,
    LocalDateTime createdAt
) {
    public static AlertDto from(Alert a, Function<Integer, String> vendorName) {
        return new AlertDto(a.getId(), a.getVendorId(), vendorName.apply(a.getVendorId()),
            a.getMessage(), a.getSeverity().name(), a.isResolved(), a.getCreatedAt());
    }
}
