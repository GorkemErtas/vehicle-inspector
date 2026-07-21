package com.gorkem.vehicle_inspector.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "damage_inspections")
public class DamageInspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "vehicle_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_inspection_vehicle"
            )
    )
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(
                    name = "fk_inspection_user"
            )
    )
    private User user;

    @Column(name = "image_path", length = 500)
    private String imagePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private InspectionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "damage_severity", length = 30)
    private DamageSeverity damageSeverity;

    @Enumerated(EnumType.STRING)
    @Column(name = "damage_type", length = 100)
    private DamageType damageType;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_part", length = 50)
    private VehiclePart vehiclePart;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommended_action", length = 50)
    private RepairAction recommendedAction;

    @Column(name = "part_replacement_required")
    private Boolean partReplacementRequired;

    @Column(name = "confidence_score")
    private Double confidenceScore;

    @Column(name = "analysis_message", length = 1000)
    private String analysisMessage;

    @Column(
            name = "created_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    protected DamageInspection() {
    }

    public DamageInspection(
            Vehicle vehicle,
            User user,
            InspectionStatus status
    ) {
        this.vehicle = vehicle;
        this.user = user;
        this.status = status;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public User getUser() {
        return user;
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

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setStatus(InspectionStatus status) {
        this.status = status;
    }

    public void setDamageSeverity(
            DamageSeverity damageSeverity
    ) {
        this.damageSeverity = damageSeverity;
    }

    public void setDamageType(DamageType damageType) {
        this.damageType = damageType;
    }

    public void setVehiclePart(VehiclePart vehiclePart) {
        this.vehiclePart = vehiclePart;
    }

    public void setRecommendedAction(
            RepairAction recommendedAction
    ) {
        this.recommendedAction = recommendedAction;
    }

    public void setPartReplacementRequired(
            Boolean partReplacementRequired
    ) {
        this.partReplacementRequired =
                partReplacementRequired;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public void setAnalysisMessage(String analysisMessage) {
        this.analysisMessage = analysisMessage;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}