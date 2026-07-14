package com.gorkem.vehicle_inspector.repository;

import com.gorkem.vehicle_inspector.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    Optional<Vehicle> findByPlate(String plate);

    boolean existsByPlate(String plate);
}