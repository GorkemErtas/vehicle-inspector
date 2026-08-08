package com.gorkem.vehicle_inspector.dto.llm;

import java.math.BigDecimal;

public class LlmInspectionReportResult {

    public String title;

    public String summary;

    public String damageDescription;

    public String repairRecommendation;

    public BigDecimal estimatedMinimumPrice;

    public BigDecimal estimatedMaximumPrice;

    public String currency;

    public String priceInformation;

    public String priceSourceDescription;

    public String disclaimer;

    public LlmInspectionReportResult() {
    }
}