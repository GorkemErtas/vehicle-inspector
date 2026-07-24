package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.VehiclePart;

import java.math.BigDecimal;

public record RepairPriceResponse(
        Long id,
        String brand,
        String model,
        Integer modelYear,
        VehiclePart vehiclePart,
        RepairAction repairAction,
        DamageSeverity damageSeverity,
        BigDecimal minimumPrice,
        BigDecimal maximumPrice,
        Boolean active
) {
}