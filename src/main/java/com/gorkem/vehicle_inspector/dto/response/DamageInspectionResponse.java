package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.DamageType;
import com.gorkem.vehicle_inspector.entity.InspectionStatus;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import java.math.BigDecimal;

import java.time.LocalDateTime;

public class DamageInspectionResponse {

    private final Long id;
    private final Long vehicleId;
    private final String vehiclePlate;
    private final Long userId;
    private final String imagePath;
    private final InspectionStatus status;
    private final DamageSeverity damageSeverity;
    private final DamageType damageType;
    private final VehiclePart vehiclePart;
    private final RepairAction recommendedAction;
    private final Boolean partReplacementRequired;
    private final Double confidenceScore;
    private final String analysisMessage;
    private final LocalDateTime createdAt;
    private final LocalDateTime completedAt;
    private final BigDecimal estimatedMinimumPrice;
    private final BigDecimal estimatedMaximumPrice;
    private final String priceCurrency;
    private final LocalDateTime priceCalculatedAt;
    private final Boolean priceAvailable;
    private final String priceMessage;

    public DamageInspectionResponse(
            Long id,
            Long vehicleId,
            String vehiclePlate,
            Long userId,
            String imagePath,
            InspectionStatus status,
            DamageSeverity damageSeverity,
            DamageType damageType,
            VehiclePart vehiclePart,
            RepairAction recommendedAction,
            Boolean partReplacementRequired,
            Double confidenceScore,
            String analysisMessage,
            LocalDateTime createdAt,
            LocalDateTime completedAt,
            BigDecimal estimatedMinimumPrice,
            BigDecimal estimatedMaximumPrice,
            String priceCurrency,
            LocalDateTime priceCalculatedAt,
            Boolean priceAvailable,
            String priceMessage
    ) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.vehiclePlate = vehiclePlate;
        this.userId = userId;
        this.imagePath = imagePath;
        this.status = status;
        this.damageSeverity = damageSeverity;
        this.damageType = damageType;
        this.vehiclePart = vehiclePart;
        this.recommendedAction = recommendedAction;
        this.partReplacementRequired = partReplacementRequired;
        this.confidenceScore = confidenceScore;
        this.analysisMessage = analysisMessage;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.estimatedMinimumPrice = estimatedMinimumPrice;
        this.estimatedMaximumPrice = estimatedMaximumPrice;
        this.priceCurrency = priceCurrency;
        this.priceCalculatedAt = priceCalculatedAt;
        this.priceAvailable = priceAvailable;
        this.priceMessage = priceMessage;
    }

    public Long getId() {
        return id;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public Long getUserId() {
        return userId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public InspectionStatus getStatus() {
        return status;
    }

    public DamageSeverity getDamageSeverity() {
        return damageSeverity;
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public VehiclePart getVehiclePart() {
        return vehiclePart;
    }

    public RepairAction getRecommendedAction() {
        return recommendedAction;
    }

    public Boolean getPartReplacementRequired() {
        return partReplacementRequired;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public String getAnalysisMessage() {
        return analysisMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public BigDecimal getEstimatedMinimumPrice() {
        return estimatedMinimumPrice;
    }

    public BigDecimal getEstimatedMaximumPrice() {
        return estimatedMaximumPrice;
    }

    public String getPriceCurrency() {
        return priceCurrency;
    }

    public LocalDateTime getPriceCalculatedAt() {
        return priceCalculatedAt;
    }

    public Boolean getPriceAvailable() {
        return priceAvailable;
    }

    public String getPriceMessage() {
        return priceMessage;
    }
}