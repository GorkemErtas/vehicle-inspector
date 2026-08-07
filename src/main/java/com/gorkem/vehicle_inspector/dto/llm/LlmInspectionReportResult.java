package com.gorkem.vehicle_inspector.dto.llm;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.math.BigDecimal;

public class LlmInspectionReportResult {

    @JsonPropertyDescription(
            "Kullanıcıya gösterilecek kısa Türkçe rapor başlığı."
    )
    public String title;

    @JsonPropertyDescription(
            "Araç hasarının kısa ve anlaşılır Türkçe özeti."
    )
    public String summary;

    @JsonPropertyDescription(
            "ML tarafından tespit edilen hasarların teknik olmayan Türkçe açıklaması."
    )
    public String damageDescription;

    @JsonPropertyDescription(
            "Tespit edilen hasarlara uygun onarım önerisinin Türkçe açıklaması."
    )
    public String repairRecommendation;

    @JsonPropertyDescription(
            "Seçilen şehirdeki güncel piyasa koşullarına göre tahmini minimum onarım fiyatı."
    )
    public BigDecimal estimatedMinimumPrice;

    @JsonPropertyDescription(
            "Seçilen şehirdeki güncel piyasa koşullarına göre tahmini maksimum onarım fiyatı."
    )
    public BigDecimal estimatedMaximumPrice;

    @JsonPropertyDescription(
            "ISO para birimi. Türkiye fiyatları için TRY."
    )
    public String currency;

    @JsonPropertyDescription(
            "Tahmini fiyat aralığının kullanıcıya açıklaması."
    )
    public String priceInformation;

    @JsonPropertyDescription(
            "Fiyat tahmininin hangi güncel piyasa verileri ve kaynak türleri dikkate alınarak oluşturulduğunun kısa açıklaması."
    )
    public String priceSourceDescription;

    @JsonPropertyDescription(
            "Fiyatın kesin servis teklifi olmadığını açıklayan kısa uyarı."
    )
    public String disclaimer;
}