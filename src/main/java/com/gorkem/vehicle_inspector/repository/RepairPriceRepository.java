package com.gorkem.vehicle_inspector.repository;

import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.RepairPrice;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepairPriceRepository
        extends JpaRepository<RepairPrice, Long> {

    Optional<RepairPrice>
    findByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairActionAndDamageSeverityAndActiveTrue(
            String brand,
            String model,
            Integer modelYear,
            VehiclePart vehiclePart,
            RepairAction repairAction,
            DamageSeverity damageSeverity
    );

    boolean existsByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairActionAndDamageSeverity(
            String brand,
            String model,
            Integer modelYear,
            VehiclePart vehiclePart,
            RepairAction repairAction,
            DamageSeverity damageSeverity
    );

    boolean existsByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairActionAndDamageSeverityAndIdNot(
            String brand,
            String model,
            Integer modelYear,
            VehiclePart vehiclePart,
            RepairAction repairAction,
            DamageSeverity damageSeverity,
            Long id
    );

    List<RepairPrice> findAllByOrderByBrandAscModelAscModelYearDesc();

    List<RepairPrice> findAllByActiveTrueOrderByBrandAscModelAsc();
}