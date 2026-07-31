package com.gorkem.vehicle_inspector.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "damage_detections")
public class DamageDetection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "inspection_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_detection_inspection"
            )
    )
    private DamageInspection inspection;

    @Column(nullable = false, length = 100)
    private String label;

    @Column(nullable = false)
    private Double confidence;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "affected_part",
            length = 50
    )
    private VehiclePart affectedPart;

    @Column(nullable = false)
    private Double x1;

    @Column(nullable = false)
    private Double y1;

    @Column(nullable = false)
    private Double x2;

    @Column(nullable = false)
    private Double y2;

    protected DamageDetection() {
    }

    public DamageDetection(
            DamageInspection inspection,
            String label,
            Double confidence,
            VehiclePart affectedPart,
            Double x1,
            Double y1,
            Double x2,
            Double y2
    ) {
        this.inspection = inspection;
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

    public DamageInspection getInspection() {
        return inspection;
    }

    public String getLabel() {
        return label;
    }

    public Double getConfidence() {
        return confidence;
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

    public VehiclePart getAffectedPart() {
        return affectedPart;
    }

    public void setInspection(
            DamageInspection inspection
    ) {
        this.inspection = inspection;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setConfidence(Double confidence) {
        this.confidence = confidence;
    }

    public void setX1(Double x1) {
        this.x1 = x1;
    }

    public void setY1(Double y1) {
        this.y1 = y1;
    }

    public void setX2(Double x2) {
        this.x2 = x2;
    }

    public void setY2(Double y2) {
        this.y2 = y2;
    }

    public void setAffectedPart(
            VehiclePart affectedPart
    ) {
        this.affectedPart = affectedPart;
    }
}