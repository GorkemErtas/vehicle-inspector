package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.RepairPrice;
import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import com.gorkem.vehicle_inspector.repository.RepairPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PriceEstimationService {

    private final RepairPriceRepository repairPriceRepository;

    public PriceEstimationService(
            RepairPriceRepository repairPriceRepository
    ) {
        this.repairPriceRepository = repairPriceRepository;
    }

    @Transactional(readOnly = true)
    public List<RepairPrice> findMatchingPrices(
            Vehicle vehicle,
            List<VehiclePart> affectedParts,
            RepairAction repairAction,
            DamageSeverity damageSeverity
    ) {
        if (vehicle == null
                || affectedParts == null
                || affectedParts.isEmpty()
                || repairAction == null
                || damageSeverity == null) {
            return List.of();
        }

        return affectedParts.stream()
                .filter(Objects::nonNull)
                .filter(part ->
                        part != VehiclePart.UNKNOWN
                )
                .distinct()
                .map(vehiclePart ->
                        repairPriceRepository
                                .findByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairActionAndDamageSeverityAndActiveTrue(
                                        vehicle.getBrand().trim(),
                                        vehicle.getModel().trim(),
                                        vehicle.getModelYear(),
                                        vehiclePart,
                                        repairAction,
                                        damageSeverity
                                )
                                .orElse(null)
                )
                .filter(Objects::nonNull)
                .toList();
    }
}