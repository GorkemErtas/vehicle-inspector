package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.VehiclePart;

import java.math.BigDecimal;

public record RepairPriceDetailResponse(
        VehiclePart vehiclePart,
        Boolean found,
        BigDecimal minimumPrice,
        BigDecimal maximumPrice
) {
}