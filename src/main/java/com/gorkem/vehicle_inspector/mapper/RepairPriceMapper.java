package com.gorkem.vehicle_inspector.mapper;

import com.gorkem.vehicle_inspector.dto.request.RepairPriceRequest;
import com.gorkem.vehicle_inspector.dto.response.RepairPriceResponse;
import com.gorkem.vehicle_inspector.entity.RepairPrice;

public final class RepairPriceMapper {

    private RepairPriceMapper() {
    }

    public static RepairPrice toEntity(
            RepairPriceRequest request
    ) {
        RepairPrice repairPrice = new RepairPrice();

        repairPrice.setBrand(
                request.brand().trim()
        );

        repairPrice.setModel(
                request.model().trim()
        );

        repairPrice.setModelYear(
                request.modelYear()
        );

        repairPrice.setVehiclePart(
                request.vehiclePart()
        );

        repairPrice.setRepairAction(
                request.repairAction()
        );

        repairPrice.setDamageSeverity(
                request.damageSeverity()
        );

        repairPrice.setMinimumPrice(
                request.minimumPrice()
        );

        repairPrice.setMaximumPrice(
                request.maximumPrice()
        );

        repairPrice.setActive(
                request.active() == null
                        ? true
                        : request.active()
        );

        return repairPrice;
    }

    public static RepairPriceResponse toResponse(
            RepairPrice repairPrice
    ) {
        return new RepairPriceResponse(
                repairPrice.getId(),
                repairPrice.getBrand(),
                repairPrice.getModel(),
                repairPrice.getModelYear(),
                repairPrice.getVehiclePart(),
                repairPrice.getRepairAction(),
                repairPrice.getDamageSeverity(),
                repairPrice.getMinimumPrice(),
                repairPrice.getMaximumPrice(),
                repairPrice.getActive()
        );
    }

    public static void updateEntity(
            RepairPrice repairPrice,
            RepairPriceRequest request
    ) {

        repairPrice.setBrand(
                request.brand().trim()
        );

        repairPrice.setModel(
                request.model().trim()
        );

        repairPrice.setModelYear(
                request.modelYear()
        );

        repairPrice.setVehiclePart(
                request.vehiclePart()
        );

        repairPrice.setRepairAction(
                request.repairAction()
        );

        repairPrice.setDamageSeverity(
                request.damageSeverity()
        );

        repairPrice.setMinimumPrice(
                request.minimumPrice()
        );

        repairPrice.setMaximumPrice(
                request.maximumPrice()
        );

        repairPrice.setActive(
                request.active() == null
                        ? true
                        : request.active()
        );
    }

}