package com.gorkem.vehicle_inspector.controller;

import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.service.VehicleService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(
            @RequestBody Vehicle vehicle
    ) {
        Vehicle savedVehicle = vehicleService.createVehicle(vehicle);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedVehicle);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(
            @PathVariable Long id
    ) {
        Vehicle vehicle = vehicleService.getVehicleById(id);

        return ResponseEntity.ok(vehicle);
    }
}