package com.gorkem.vehicle_inspector.dto.request;

import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record RepairPriceRequest(

        @NotBlank(message = "Marka boş bırakılamaz.")
        @Size(max = 100)
        String brand,

        @NotBlank(message = "Model boş bırakılamaz.")
        @Size(max = 100)
        String model,

        @NotNull(message = "Model yılı zorunludur.")
        @Min(value = 1950, message = "Model yılı 1950'den küçük olamaz.")
        @Max(value = 2100, message = "Model yılı 2100'den büyük olamaz.")
        Integer modelYear,

        @NotNull(message = "Araç parçası zorunludur.")
        VehiclePart vehiclePart,

        @NotNull(message = "Onarım işlemi zorunludur.")
        RepairAction repairAction,

        @NotNull(message = "Minimum fiyat zorunludur.")
        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "Minimum fiyat negatif olamaz."
        )
        BigDecimal minimumPrice,

        @NotNull(message = "Maksimum fiyat zorunludur.")
        @DecimalMin(
                value = "0.00",
                inclusive = true,
                message = "Maksimum fiyat negatif olamaz."
        )
        BigDecimal maximumPrice,

        Boolean active
) {
}