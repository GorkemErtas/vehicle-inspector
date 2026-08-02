package com.gorkem.vehicle_inspector.mapper;

import com.gorkem.vehicle_inspector.dto.response.DamageDetectionResponse;
import com.gorkem.vehicle_inspector.dto.response.DamageInspectionResponse;
import com.gorkem.vehicle_inspector.entity.DamageInspection;
import java.util.List;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import java.util.Objects;
import com.gorkem.vehicle_inspector.dto.response.RepairPriceDetailResponse;
import com.gorkem.vehicle_inspector.dto.response.DamageRepairRecommendationResponse;
import com.gorkem.vehicle_inspector.entity.DamageType;

public final class DamageInspectionMapper {

    private static final double AFFECTED_PART_CONFIDENCE_THRESHOLD = 0.50;

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
                        .filter(detection ->
                                detection.getConfidence() != null
                                        && detection.getConfidence()
                                        >= AFFECTED_PART_CONFIDENCE_THRESHOLD
                        )
                        .map(detection ->
                                detection.getAffectedPart()
                        )
                        .filter(Objects::nonNull)
                        .filter(part ->
                                part != VehiclePart.UNKNOWN
                        )
                        .distinct()
                        .toList();

        List<DamageRepairRecommendationResponse>
                repairRecommendations =
                inspection.getRepairRecommendations()
                        .stream()
                        .map(recommendation ->
                                new DamageRepairRecommendationResponse(
                                        recommendation.getDamageType(),
                                        recommendation.getRecommendedAction(),
                                        recommendation.getPartReplacementRequired(),
                                        recommendation.getAffectedParts()
                                                .stream()
                                                .toList()
                                )
                        )
                        .toList();

        List<DamageType> damageTypes =
                repairRecommendations.stream()
                        .map(
                                DamageRepairRecommendationResponse::damageType
                        )
                        .distinct()
                        .toList();

        List<RepairPriceDetailResponse> priceDetails =
                inspection.getPriceDetails()
                        .stream()
                        .map(priceDetail ->
                                new RepairPriceDetailResponse(
                                        priceDetail.getVehiclePart(),
                                        priceDetail.getDamageType(),
                                        priceDetail.getRepairAction(),
                                        priceDetail.getPriceFound(),
                                        priceDetail.getMinimumPrice(),
                                        priceDetail.getMaximumPrice()
                                )
                        )
                        .toList();

        return new DamageInspectionResponse(
                inspection.getId(),
                inspection.getVehicle().getId(),
                inspection.getVehicle().getPlate(),
                inspection.getUser().getId(),
                inspection.getImagePath(),
                inspection.getStatus(),
                inspection.getDamageSeverity(),
                damageTypes,
                affectedParts,
                repairRecommendations,
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
                priceDetails,
                detections
        );
    }
}