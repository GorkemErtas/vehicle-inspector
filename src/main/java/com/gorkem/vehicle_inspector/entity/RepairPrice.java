package com.gorkem.vehicle_inspector.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(
        name = "repair_prices",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_repair_price_configuration",
                        columnNames = {
                                "brand",
                                "model",
                                "model_year",
                                "vehicle_part",
                                "repair_action"
                        }
                )
        },
        indexes = {
                @Index(
                        name = "idx_repair_price_lookup",
                        columnList =
                                "brand, model, model_year, vehicle_part, repair_action"
                )
        }
)
public class RepairPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String brand;

    @Column(nullable = false, length = 100)
    private String model;

    @Column(name = "model_year", nullable = false)
    private Integer modelYear;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "vehicle_part",
            nullable = false,
            length = 50
    )
    private VehiclePart vehiclePart;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "repair_action",
            nullable = false,
            length = 50
    )
    private RepairAction repairAction;

    @Column(
            name = "minimum_price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal minimumPrice;

    @Column(
            name = "maximum_price",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal maximumPrice;

    @Column(nullable = false)
    private Boolean active = true;

    public RepairPrice() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getModelYear() {
        return modelYear;
    }

    public void setModelYear(Integer modelYear) {
        this.modelYear = modelYear;
    }

    public VehiclePart getVehiclePart() {
        return vehiclePart;
    }

    public void setVehiclePart(VehiclePart vehiclePart) {
        this.vehiclePart = vehiclePart;
    }

    public RepairAction getRepairAction() {
        return repairAction;
    }

    public void setRepairAction(RepairAction repairAction) {
        this.repairAction = repairAction;
    }

    public BigDecimal getMinimumPrice() {
        return minimumPrice;
    }

    public void setMinimumPrice(BigDecimal minimumPrice) {
        this.minimumPrice = minimumPrice;
    }

    public BigDecimal getMaximumPrice() {
        return maximumPrice;
    }

    public void setMaximumPrice(BigDecimal maximumPrice) {
        this.maximumPrice = maximumPrice;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}