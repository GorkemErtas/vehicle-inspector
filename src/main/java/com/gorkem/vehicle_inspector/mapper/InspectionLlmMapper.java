package com.gorkem.vehicle_inspector.mapper;

import com.gorkem.vehicle_inspector.dto.llm.DamageContext;
import com.gorkem.vehicle_inspector.dto.llm.InspectionLlmRequest;
import com.gorkem.vehicle_inspector.dto.llm.VehicleContext;
import com.gorkem.vehicle_inspector.entity.DamageInspection;
import com.gorkem.vehicle_inspector.entity.DamageRepairRecommendation;

import java.time.LocalDate;
import java.util.List;

public final class InspectionLlmMapper {

    private InspectionLlmMapper() {
    }

    public static InspectionLlmRequest toRequest(
            DamageInspection inspection
    ) {
        VehicleContext vehicle =
                new VehicleContext(
                        inspection.getVehicle().getBrand(),
                        inspection.getVehicle().getModel(),
                        inspection.getVehicle().getModelYear(),
                        inspection.getVehicle().getMileage()
                );

        List<DamageContext> damages =
                inspection.getRepairRecommendations()
                        .stream()
                        .map(
                                InspectionLlmMapper
                                        ::toDamageContext
                        )
                        .toList();

        return new InspectionLlmRequest(
                vehicle,
                inspection.getLocationCity(),
                LocalDate.now(),
                inspection.getDamageSeverity(),
                inspection.getConfidenceScore(),
                inspection.getAnalysisMessage(),
                damages
        );
    }

    private static DamageContext toDamageContext(
            DamageRepairRecommendation recommendation
    ) {
        return new DamageContext(
                recommendation.getDamageType(),
                recommendation.getRecommendedAction(),
                recommendation.getPartReplacementRequired(),
                recommendation.getAffectedParts()
                        .stream()
                        .toList()
        );
    }
}