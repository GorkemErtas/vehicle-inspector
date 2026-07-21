package com.gorkem.vehicle_inspector.repository;

import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.RepairPrice;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepairPriceRepository
        extends JpaRepository<RepairPrice, Long> {

    Optional<RepairPrice>
    findByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairActionAndActiveTrue(
            String brand,
            String model,
            Integer modelYear,
            VehiclePart vehiclePart,
            RepairAction repairAction
    );

    List<RepairPrice> findAllByActiveTrue();

    boolean existsByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairAction(
            String brand,
            String model,
            Integer modelYear,
            VehiclePart vehiclePart,
            RepairAction repairAction
    );
}