package com.gorkem.vehicle_inspector.mapper;

import com.gorkem.vehicle_inspector.dto.request.CreateVehicleRequest;
import com.gorkem.vehicle_inspector.dto.response.VehicleResponse;
import com.gorkem.vehicle_inspector.entity.Vehicle;

public final class VehicleMapper {

    private VehicleMapper() {
    }

    public static Vehicle toEntity(CreateVehicleRequest request) {
        return new Vehicle(
                request.getPlate().trim().toUpperCase(),
                request.getBrand().trim(),
                request.getModel().trim(),
                request.getModelYear(),
                request.getMileage()
        );
    }

    public static VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse(
                vehicle.getId(),
                vehicle.getPlate(),
                vehicle.getBrand(),
                vehicle.getModel(),
                vehicle.getModelYear(),
                vehicle.getMileage()
        );
    }
}