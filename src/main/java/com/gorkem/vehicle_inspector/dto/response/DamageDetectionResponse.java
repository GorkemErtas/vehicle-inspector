package com.gorkem.vehicle_inspector.dto.response;

import com.gorkem.vehicle_inspector.entity.VehiclePart;

public class DamageDetectionResponse {

    private final Long id;
    private final String label;
    private final Double confidence;
    private final VehiclePart affectedPart;
    private final Double x1;
    private final Double y1;
    private final Double x2;
    private final Double y2;

    public DamageDetectionResponse(
            Long id,
            String label,
            Double confidence,
            VehiclePart affectedPart,
            Double x1,
            Double y1,
            Double x2,
            Double y2
    ) {
        this.id = id;
        this.label = label;
        this.confidence = confidence;
        this.affectedPart = affectedPart;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public Double getConfidence() {
        return confidence;
    }

    public VehiclePart getAffectedPart() {
        return affectedPart;
    }

    public Double getX1() {
        return x1;
    }

    public Double getY1() {
        return y1;
    }

    public Double getX2() {
        return x2;
    }

    public Double getY2() {
        return y2;
    }
}