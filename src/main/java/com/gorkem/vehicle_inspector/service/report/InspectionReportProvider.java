package com.gorkem.vehicle_inspector.service.report;

import com.gorkem.vehicle_inspector.dto.response.InspectionReportResponse;
import com.gorkem.vehicle_inspector.entity.DamageInspection;

public interface InspectionReportProvider {

    InspectionReportResponse generateReport(
            DamageInspection inspection
    );

}