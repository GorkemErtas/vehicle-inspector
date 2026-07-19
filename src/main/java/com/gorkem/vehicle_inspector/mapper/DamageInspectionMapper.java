package com.gorkem.vehicle_inspector.mapper;

import com.gorkem.vehicle_inspector.dto.response.DamageInspectionResponse;
import com.gorkem.vehicle_inspector.entity.DamageInspection;

public final class DamageInspectionMapper {

    private DamageInspectionMapper() {
    }

    public static DamageInspectionResponse toResponse(
            DamageInspection inspection
    ) {
        return new DamageInspectionResponse(
                inspection.getId(),
                inspection.getVehicle().getId(),
                inspection.getVehicle().getPlate(),
                inspection.getUser().getId(),
                inspection.getImagePath(),
                inspection.getStatus(),
                inspection.getDamageSeverity(),
                inspection.getDamageType(),
                inspection.getConfidenceScore(),
                inspection.getEstimatedCost(),
                inspection.getAnalysisMessage(),
                inspection.getCreatedAt(),
                inspection.getCompletedAt()
        );
    }
}