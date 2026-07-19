package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.InspectionStatus;

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
    private final String damageType;
    private final Double confidenceScore;
    private final BigDecimal estimatedCost;
    private final String analysisMessage;
    private final LocalDateTime createdAt;
    private final LocalDateTime completedAt;

    public DamageInspectionResponse(
            Long id,
            Long vehicleId,
            String vehiclePlate,
            Long userId,
            String imagePath,
            InspectionStatus status,
            DamageSeverity damageSeverity,
            String damageType,
            Double confidenceScore,
            BigDecimal estimatedCost,
            String analysisMessage,
            LocalDateTime createdAt,
            LocalDateTime completedAt
    ) {
        this.id = id;
        this.vehicleId = vehicleId;
        this.vehiclePlate = vehiclePlate;
        this.userId = userId;
        this.imagePath = imagePath;
        this.status = status;
        this.damageSeverity = damageSeverity;
        this.damageType = damageType;
        this.confidenceScore = confidenceScore;
        this.estimatedCost = estimatedCost;
        this.analysisMessage = analysisMessage;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
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

    public String getDamageType() {
        return damageType;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public BigDecimal getEstimatedCost() {
        return estimatedCost;
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
}