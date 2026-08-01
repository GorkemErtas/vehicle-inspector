package com.gorkem.vehicle_inspector.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "damage_inspection_price_details")
public class DamageInspectionPriceDetail {

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
                    name = "fk_price_detail_inspection"
            )
    )
    private DamageInspection inspection;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "vehicle_part",
            nullable = false,
            length = 50
    )
    private VehiclePart vehiclePart;

    @Column(name = "price_found", nullable = false)
    private Boolean priceFound;

    @Column(
            name = "minimum_price",
            precision = 12,
            scale = 2
    )
    private BigDecimal minimumPrice;

    @Column(
            name = "maximum_price",
            precision = 12,
            scale = 2
    )
    private BigDecimal maximumPrice;

    protected DamageInspectionPriceDetail() {
    }

    public DamageInspectionPriceDetail(
            DamageInspection inspection,
            VehiclePart vehiclePart,
            Boolean priceFound,
            BigDecimal minimumPrice,
            BigDecimal maximumPrice
    ) {
        this.inspection = inspection;
        this.vehiclePart = vehiclePart;
        this.priceFound = priceFound;
        this.minimumPrice = minimumPrice;
        this.maximumPrice = maximumPrice;
    }

    public Long getId() {
        return id;
    }

    public DamageInspection getInspection() {
        return inspection;
    }

    public VehiclePart getVehiclePart() {
        return vehiclePart;
    }

    public Boolean getPriceFound() {
        return priceFound;
    }

    public BigDecimal getMinimumPrice() {
        return minimumPrice;
    }

    public BigDecimal getMaximumPrice() {
        return maximumPrice;
    }

    public void setInspection(
            DamageInspection inspection
    ) {
        this.inspection = inspection;
    }
}