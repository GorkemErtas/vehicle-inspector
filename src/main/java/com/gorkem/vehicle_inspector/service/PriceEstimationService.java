package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.entity.DamageRepairRecommendation;
import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.RepairPrice;
import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import com.gorkem.vehicle_inspector.repository.RepairPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
            List<DamageRepairRecommendation>
                    repairRecommendations,
            DamageSeverity damageSeverity
    ) {
        if (vehicle == null
                || repairRecommendations == null
                || repairRecommendations.isEmpty()
                || damageSeverity == null) {
            return List.of();
        }

        List<RepairPrice> matchingPrices =
                new ArrayList<>();

        Set<String> processedConfigurations =
                new HashSet<>();

        for (DamageRepairRecommendation recommendation
                : repairRecommendations) {

            if (recommendation == null
                    || recommendation.getRecommendedAction() == null
                    || recommendation.getAffectedParts() == null) {
                continue;
            }

            for (VehiclePart vehiclePart
                    : recommendation.getAffectedParts()) {

                if (vehiclePart == null
                        || vehiclePart == VehiclePart.UNKNOWN) {
                    continue;
                }

                String configurationKey =
                        vehiclePart.name()
                                + ":"
                                + recommendation
                                .getRecommendedAction()
                                .name();

                if (!processedConfigurations.add(
                        configurationKey
                )) {
                    continue;
                }

                repairPriceRepository
                        .findByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairActionAndDamageSeverityAndActiveTrue(
                                vehicle.getBrand().trim(),
                                vehicle.getModel().trim(),
                                vehicle.getModelYear(),
                                vehiclePart,
                                recommendation
                                        .getRecommendedAction(),
                                damageSeverity
                        )
                        .ifPresent(
                                matchingPrices::add
                        );
            }
        }

        return matchingPrices;
    }
}