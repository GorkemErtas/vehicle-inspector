package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.dto.request.CreateVehicleRequest;
import com.gorkem.vehicle_inspector.dto.response.VehicleResponse;
import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.mapper.VehicleMapper;
import com.gorkem.vehicle_inspector.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponse createVehicle(CreateVehicleRequest request) {
        String normalizedPlate = request.getPlate()
                .trim()
                .toUpperCase();

        if (vehicleRepository.existsByPlate(normalizedPlate)) {
            throw new IllegalArgumentException(
                    "Bu plakaya ait araç zaten kayıtlı: " + normalizedPlate
            );
        }

        Vehicle vehicle = VehicleMapper.toEntity(request);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return VehicleMapper.toResponse(savedVehicle);
    }

    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(VehicleMapper::toResponse)
                .toList();
    }

    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Araç bulunamadı. ID: " + id
                        )
                );

        return VehicleMapper.toResponse(vehicle);
    }
}