package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.dto.request.CreateVehicleRequest;
import com.gorkem.vehicle_inspector.dto.request.UpdateVehicleRequest;
import com.gorkem.vehicle_inspector.dto.response.VehicleResponse;
import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.exception.DuplicateResourceException;
import com.gorkem.vehicle_inspector.exception.ResourceNotFoundException;
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
        String normalizedPlate = normalizePlate(request.getPlate());

        if (vehicleRepository.existsByPlate(normalizedPlate)) {
            throw new DuplicateResourceException(
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
        Vehicle vehicle = findVehicleById(id);

        return VehicleMapper.toResponse(vehicle);
    }

    public VehicleResponse updateVehicle(
            Long id,
            UpdateVehicleRequest request
    ) {
        Vehicle vehicle = findVehicleById(id);

        String normalizedPlate = normalizePlate(request.getPlate());

        if (vehicleRepository.existsByPlateAndIdNot(normalizedPlate, id)) {
            throw new DuplicateResourceException(
                    "Bu plaka başka bir araç tarafından kullanılıyor: "
                            + normalizedPlate
            );
        }

        VehicleMapper.updateEntity(vehicle, request);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return VehicleMapper.toResponse(updatedVehicle);
    }

    public void deleteVehicle(Long id) {
        Vehicle vehicle = findVehicleById(id);

        vehicleRepository.delete(vehicle);
    }

    private Vehicle findVehicleById(Long id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Araç bulunamadı. ID: " + id
                        )
                );
    }

    private String normalizePlate(String plate) {
        return plate.trim().toUpperCase();
    }
}