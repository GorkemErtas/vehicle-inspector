package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.DamageType;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.VehiclePart;

public class AiAnalysisResponse {

    private DamageType damageType;

    private DamageSeverity damageSeverity;

    private VehiclePart vehiclePart;

    private RepairAction recommendedAction;

    private Boolean partReplacementRequired;

    private Double confidenceScore;

    private String analysisMessage;

    public AiAnalysisResponse() {
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public DamageSeverity getDamageSeverity() {
        return damageSeverity;
    }

    public void setDamageSeverity(
            DamageSeverity damageSeverity
    ) {
        this.damageSeverity = damageSeverity;
    }

    public VehiclePart getVehiclePart() {
        return vehiclePart;
    }

    public void setVehiclePart(VehiclePart vehiclePart) {
        this.vehiclePart = vehiclePart;
    }

    public RepairAction getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(
            RepairAction recommendedAction
    ) {
        this.recommendedAction = recommendedAction;
    }

    public Boolean getPartReplacementRequired() {
        return partReplacementRequired;
    }

    public void setPartReplacementRequired(
            Boolean partReplacementRequired
    ) {
        this.partReplacementRequired =
                partReplacementRequired;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(
            Double confidenceScore
    ) {
        this.confidenceScore = confidenceScore;
    }

    public String getAnalysisMessage() {
        return analysisMessage;
    }

    public void setAnalysisMessage(
            String analysisMessage
    ) {
        this.analysisMessage = analysisMessage;
    }
}