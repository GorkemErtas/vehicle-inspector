package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.DamageType;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.VehiclePart;

import java.util.List;

public record DamageRepairRecommendationResponse(
        DamageType damageType,
        RepairAction recommendedAction,
        Boolean partReplacementRequired,
        List<VehiclePart> affectedParts
) {
}