package com.gorkem.vehicle_inspector.dto.response;

public class DetectedObjectResponse {

    private String label;
    private Double confidence;
    private BoundingBoxResponse boundingBox;

    public DetectedObjectResponse() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Double getConfidence() {
        return confidence;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public BoundingBoxResponse getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(
            BoundingBoxResponse boundingBox
    ) {
        this.boundingBox = boundingBox;
    }
}