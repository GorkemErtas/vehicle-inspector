package com.gorkem.vehicle_inspector.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(
            mappedBy = "inspection",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DamageDetection> detections =
            new ArrayList<>();

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

    @Column(
            name = "estimated_minimum_price",
            precision = 12,
            scale = 2
    )
    private BigDecimal estimatedMinimumPrice;

    @Column(
            name = "estimated_maximum_price",
            precision = 12,
            scale = 2
    )
    private BigDecimal estimatedMaximumPrice;

    @Column(
            name = "price_currency",
            length = 10
    )
    private String priceCurrency;

    @Column(name = "price_calculated_at")
    private LocalDateTime priceCalculatedAt;

    @Column(name = "price_available")
    private Boolean priceAvailable;

    @Column(
            name = "price_message",
            length = 500
    )
    private String priceMessage;

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

    public List<DamageDetection> getDetections() {
        return detections;
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

    public void setEstimatedMinimumPrice(
            BigDecimal estimatedMinimumPrice
    ) {
        this.estimatedMinimumPrice = estimatedMinimumPrice;
    }

    public void setEstimatedMaximumPrice(
            BigDecimal estimatedMaximumPrice
    ) {
        this.estimatedMaximumPrice = estimatedMaximumPrice;
    }

    public void setPriceCurrency(String priceCurrency) {
        this.priceCurrency = priceCurrency;
    }

    public void setPriceCalculatedAt(
            LocalDateTime priceCalculatedAt
    ) {
        this.priceCalculatedAt = priceCalculatedAt;
    }

    public void setPriceAvailable(Boolean priceAvailable) {
        this.priceAvailable = priceAvailable;
    }

    public void setPriceMessage(String priceMessage) {
        this.priceMessage = priceMessage;
    }

    public void addDetection(
            DamageDetection detection
    ) {
        detections.add(detection);
        detection.setInspection(this);
    }

    public void clearDetections() {
        detections.clear();
    }
}