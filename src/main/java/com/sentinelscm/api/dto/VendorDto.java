package com.sentinelscm.api.dto;

import com.sentinelscm.domain.Vendor;

import java.time.LocalDateTime;

public record VendorDto(
    Integer vendorId,
    String name,
    String contact,
    double rating,
    double riskScore,
    String riskLevel,
    String status,
    LocalDateTime createdAt
) {
    public static VendorDto from(Vendor v) {
        return new VendorDto(v.getId(), v.getName(), v.getContact(), v.getRating(), v.getRiskScore(),
            v.riskLevel().name(), v.getStatus().name(), v.getCreatedAt());
    }
}
