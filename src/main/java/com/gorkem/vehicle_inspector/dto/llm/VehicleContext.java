package com.gorkem.vehicle_inspector.dto.llm;

public record VehicleContext(
        String brand,
        String model,
        Integer modelYear,
        Integer mileage
) {
}