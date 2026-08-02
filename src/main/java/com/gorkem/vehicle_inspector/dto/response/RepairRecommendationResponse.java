package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.DamageType;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.VehiclePart;

import java.util.List;

public class RepairRecommendationResponse {

    private DamageType damageType;
    private RepairAction recommendedAction;
    private Boolean partReplacementRequired;
    private List<VehiclePart> affectedParts;

    public RepairRecommendationResponse() {
    }

    public DamageType getDamageType() {
        return damageType;
    }

    public void setDamageType(
            DamageType damageType
    ) {
        this.damageType = damageType;
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

    public List<VehiclePart> getAffectedParts() {
        return affectedParts;
    }

    public void setAffectedParts(
            List<VehiclePart> affectedParts
    ) {
        this.affectedParts = affectedParts;
    }
}