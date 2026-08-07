package com.gorkem.vehicle_inspector.mapper;

import com.gorkem.vehicle_inspector.dto.response.InspectionReportResponse;
import com.gorkem.vehicle_inspector.entity.InspectionReport;

public final class InspectionReportMapper {

    private InspectionReportMapper() {
    }

    public static InspectionReportResponse toResponse(
            InspectionReport report
    ) {
        if (report == null) {
            return null;
        }

        return new InspectionReportResponse(
                report.getTitle(),
                report.getSummary(),
                report.getDamageDescription(),
                report.getRepairRecommendation(),
                report.getEstimatedMinimumPrice(),
                report.getEstimatedMaximumPrice(),
                report.getCurrency(),
                report.getPriceInformation(),
                report.getPriceSourceDescription(),
                report.getDisclaimer(),
                report.getGeneratedAt()
        );
    }
}