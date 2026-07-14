package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle createVehicle(Vehicle vehicle) {
        if (vehicleRepository.existsByPlate(vehicle.getPlate())) {
            throw new IllegalArgumentException(
                    "Bu plakaya ait araç zaten kayıtlı: " + vehicle.getPlate()
            );
        }

        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Araç bulunamadı. ID: " + id
                        )
                );
    }
}
