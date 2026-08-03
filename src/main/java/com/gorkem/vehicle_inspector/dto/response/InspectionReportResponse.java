package com.gorkem.vehicle_inspector.dto.response;

public record InspectionReportResponse(
        String title,
        String summary,
        String damageDescription,
        String repairRecommendation,
        String priceInformation,
        String disclaimer
) {
}