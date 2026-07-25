package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.RepairPrice;
import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import com.gorkem.vehicle_inspector.repository.RepairPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PriceEstimationService {

    private final RepairPriceRepository repairPriceRepository;

    public PriceEstimationService(
            RepairPriceRepository repairPriceRepository
    ) {
        this.repairPriceRepository = repairPriceRepository;
    }

    @Transactional(readOnly = true)
    public Optional<RepairPrice> findMatchingPrice(
            Vehicle vehicle,
            VehiclePart vehiclePart,
            RepairAction repairAction,
            DamageSeverity damageSeverity
    ) {
        return repairPriceRepository
                .findByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairActionAndDamageSeverityAndActiveTrue(
                        vehicle.getBrand().trim(),
                        vehicle.getModel().trim(),
                        vehicle.getModelYear(),
                        vehiclePart,
                        repairAction,
                        damageSeverity
                );
    }
}