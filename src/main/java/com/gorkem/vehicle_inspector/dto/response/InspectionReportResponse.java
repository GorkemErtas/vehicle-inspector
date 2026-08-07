package com.gorkem.vehicle_inspector.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record InspectionReportResponse(

        String title,

        String summary,

        String damageDescription,

        String repairRecommendation,

        BigDecimal estimatedMinimumPrice,

        BigDecimal estimatedMaximumPrice,

        String currency,

        String priceInformation,

        String priceSourceDescription,

        String disclaimer,

        LocalDateTime generatedAt

) {
}