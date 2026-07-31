package com.gorkem.vehicle_inspector.mapper;

import com.gorkem.vehicle_inspector.dto.response.DamageInspectionResponse;
import com.gorkem.vehicle_inspector.entity.DamageInspection;
import com.gorkem.vehicle_inspector.dto.response.DamageDetectionResponse;
import java.util.List;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import java.util.Objects;

public final class DamageInspectionMapper {

    private DamageInspectionMapper() {
    }

    public static DamageInspectionResponse toResponse(
            DamageInspection inspection
    ) {

        List<DamageDetectionResponse> detections =
                inspection.getDetections()
                        .stream()
                        .map(detection ->
                                new DamageDetectionResponse(
                                        detection.getId(),
                                        detection.getLabel(),
                                        detection.getConfidence(),
                                        detection.getAffectedPart(),
                                        detection.getX1(),
                                        detection.getY1(),
                                        detection.getX2(),
                                        detection.getY2()
                                )
                        )
                        .toList();

        List<VehiclePart> affectedParts =
                inspection.getDetections()
                        .stream()
                        .map(detection ->
                                detection.getAffectedPart()
                        )
                        .filter(Objects::nonNull)
                        .filter(part ->
                                part != VehiclePart.UNKNOWN
                        )
                        .distinct()
                        .toList();

        return new DamageInspectionResponse(
                inspection.getId(),
                inspection.getVehicle().getId(),
                inspection.getVehicle().getPlate(),
                inspection.getUser().getId(),
                inspection.getImagePath(),
                inspection.getStatus(),
                inspection.getDamageSeverity(),
                inspection.getDamageType(),
                inspection.getVehiclePart(),
                affectedParts,
                inspection.getRecommendedAction(),
                inspection.getPartReplacementRequired(),
                inspection.getConfidenceScore(),
                inspection.getAnalysisMessage(),
                inspection.getCreatedAt(),
                inspection.getCompletedAt(),
                inspection.getEstimatedMinimumPrice(),
                inspection.getEstimatedMaximumPrice(),
                inspection.getPriceCurrency(),
                inspection.getPriceCalculatedAt(),
                inspection.getPriceAvailable(),
                inspection.getPriceMessage(),
                detections
        );
    }
}