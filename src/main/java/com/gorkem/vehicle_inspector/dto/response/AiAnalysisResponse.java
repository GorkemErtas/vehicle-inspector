package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.DamageType;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AiAnalysisResponse {

    private DamageType damageType;

    private DamageSeverity damageSeverity;

    private RepairAction recommendedAction;

    private Boolean partReplacementRequired;

    private Double confidenceScore;

    private String analysisMessage;

    private List<DetectedObjectResponse> detections;

    private List<VehiclePart> affectedParts;

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

    public List<DetectedObjectResponse> getDetections() {
        return detections;
    }

    public void setDetections(
            List<DetectedObjectResponse> detections
    ) {
        this.detections = detections;
    }

    public List<VehiclePart> getAffectedParts() {
        return affectedParts;
    }

    public void setAffectedParts(
            List<VehiclePart> affectedParts
    ) {
        this.affectedParts = affectedParts;
    }
}