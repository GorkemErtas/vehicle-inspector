package com.gorkem.vehicle_inspector.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "inspection_reports")
public class InspectionReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "inspection_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(
                    name = "fk_report_inspection"
            )
    )
    private DamageInspection inspection;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, length = 1500)
    private String summary;

    @Column(
            name = "damage_description",
            nullable = false,
            length = 3000
    )
    private String damageDescription;

    @Column(
            name = "repair_recommendation",
            nullable = false,
            length = 3000
    )
    private String repairRecommendation;

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

    @Column(length = 10)
    private String currency;

    @Column(
            name = "price_information",
            length = 3000
    )
    private String priceInformation;

    @Column(
            name = "price_source_description",
            length = 2000
    )
    private String priceSourceDescription;

    @Column(
            nullable = false,
            length = 1500
    )
    private String disclaimer;

    @Column(
            name = "generated_at",
            nullable = false,
            updatable = false
    )
    private LocalDateTime generatedAt;

    protected InspectionReport() {
    }

    public InspectionReport(
            DamageInspection inspection,
            String title,
            String summary,
            String damageDescription,
            String repairRecommendation,
            BigDecimal estimatedMinimumPrice,
            BigDecimal estimatedMaximumPrice,
            String currency,
            String priceInformation,
            String priceSourceDescription,
            String disclaimer
    ) {
        this.inspection = inspection;
        this.title = title;
        this.summary = summary;
        this.damageDescription = damageDescription;
        this.repairRecommendation = repairRecommendation;
        this.estimatedMinimumPrice =
                estimatedMinimumPrice;
        this.estimatedMaximumPrice =
                estimatedMaximumPrice;
        this.currency = currency;
        this.priceInformation = priceInformation;
        this.priceSourceDescription =
                priceSourceDescription;
        this.disclaimer = disclaimer;
    }

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public DamageInspection getInspection() {
        return inspection;
    }

    public String getTitle() {
        return title;
    }

    public String getSummary() {
        return summary;
    }

    public String getDamageDescription() {
        return damageDescription;
    }

    public String getRepairRecommendation() {
        return repairRecommendation;
    }

    public BigDecimal getEstimatedMinimumPrice() {
        return estimatedMinimumPrice;
    }

    public BigDecimal getEstimatedMaximumPrice() {
        return estimatedMaximumPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPriceInformation() {
        return priceInformation;
    }

    public String getPriceSourceDescription() {
        return priceSourceDescription;
    }

    public String getDisclaimer() {
        return disclaimer;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setInspection(
            DamageInspection inspection
    ) {
        this.inspection = inspection;
    }

    public void updateFrom(
            InspectionReport source
    ) {
        if (source == null) {
            throw new IllegalArgumentException(
                    "Güncellenecek rapor boş olamaz."
            );
        }

        this.title = source.getTitle();
        this.summary = source.getSummary();
        this.damageDescription =
                source.getDamageDescription();
        this.repairRecommendation =
                source.getRepairRecommendation();

        this.estimatedMinimumPrice =
                source.getEstimatedMinimumPrice();

        this.estimatedMaximumPrice =
                source.getEstimatedMaximumPrice();

        this.currency = source.getCurrency();

        this.priceInformation =
                source.getPriceInformation();

        this.priceSourceDescription =
                source.getPriceSourceDescription();

        this.disclaimer =
                source.getDisclaimer();

        this.generatedAt =
                LocalDateTime.now();
    }
}