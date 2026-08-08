package com.gorkem.vehicle_inspector.service.report;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.Schema;
import com.google.genai.types.Type;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import com.google.genai.types.GoogleSearch;
import com.google.genai.types.Tool;
import com.gorkem.vehicle_inspector.dto.llm.InspectionLlmRequest;
import com.gorkem.vehicle_inspector.dto.llm.LlmInspectionReportResult;
import com.gorkem.vehicle_inspector.entity.DamageInspection;
import com.gorkem.vehicle_inspector.entity.InspectionReport;
import com.gorkem.vehicle_inspector.mapper.InspectionLlmMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class GeminiInspectionReportService {

    private final Client geminiClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public GeminiInspectionReportService(
            Client geminiClient,
            ObjectMapper objectMapper,
            @Value("${application.gemini.model}")
            String model
    ) {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
        this.model = model;
    }

    public InspectionReport generateReport(
            DamageInspection inspection
    ) {

        InspectionLlmRequest request =
                InspectionLlmMapper.toRequest(
                        inspection
                );

        String prompt =
                InspectionReportPromptBuilder.build(
                        request
                );

        /* Gemini Paid Plan
        Tool googleSearchTool =
                Tool.builder()
                        .googleSearch(
                                GoogleSearch.builder()
                                        .build()
                        )
                        .build();  */

        GenerateContentConfig config =
                GenerateContentConfig.builder()
                        /* Gemini Paid Plan
                        .tools(googleSearchTool) */
                        .responseMimeType(
                                "application/json"
                        )
                        .responseSchema(
                                createResponseSchema()
                        )
                        .candidateCount(1)
                        .build();

        GenerateContentResponse response =
                geminiClient.models.generateContent(
                        model,
                        prompt,
                        config
                );

        String responseText =
                response.text();

        if (responseText == null
                || responseText.isBlank()) {

            throw new IllegalStateException(
                    "Gemini boş rapor döndürdü."
            );
        }

        LlmInspectionReportResult result =
                parseResponse(
                        responseText
                );

        validateResult(result);

        return new InspectionReport(
                inspection,
                result.title,
                result.summary,
                result.damageDescription,
                result.repairRecommendation,
                result.estimatedMinimumPrice,
                result.estimatedMaximumPrice,
                result.currency,
                result.priceInformation,
                result.priceSourceDescription,
                result.disclaimer
        );
    }

    private Schema createResponseSchema() {

        return Schema.builder()
                .type(Type.Known.OBJECT)
                .properties(
                        java.util.Map.of(
                                "title",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .build(),

                                "summary",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .build(),

                                "damageDescription",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .build(),

                                "repairRecommendation",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .build(),

                                "estimatedMinimumPrice",
                                Schema.builder()
                                        .type(Type.Known.NUMBER)
                                        .build(),

                                "estimatedMaximumPrice",
                                Schema.builder()
                                        .type(Type.Known.NUMBER)
                                        .build(),

                                "currency",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .enum_(
                                                java.util.List.of(
                                                        "TRY"
                                                )
                                        )
                                        .build(),

                                "priceInformation",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .build(),

                                "priceSourceDescription",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .build(),

                                "disclaimer",
                                Schema.builder()
                                        .type(Type.Known.STRING)
                                        .build()
                        )
                )
                .required(
                        java.util.List.of(
                                "title",
                                "summary",
                                "damageDescription",
                                "repairRecommendation",
                                "estimatedMinimumPrice",
                                "estimatedMaximumPrice",
                                "currency",
                                "priceInformation",
                                "priceSourceDescription",
                                "disclaimer"
                        )
                )
                .build();
    }

    private LlmInspectionReportResult parseResponse(
            String responseText
    ) {

        try {
            return objectMapper.readValue(
                    responseText,
                    LlmInspectionReportResult.class
            );

        } catch (JsonProcessingException exception) {

            throw new IllegalStateException(
                    "Gemini raporu JSON formatında okunamadı.",
                    exception
            );
        }
    }

    private void validateResult(
            LlmInspectionReportResult result
    ) {

        if (result == null) {
            throw new IllegalStateException(
                    "Gemini raporu oluşturulamadı."
            );
        }

        if (result.title == null
                || result.title.isBlank()) {

            throw new IllegalStateException(
                    "Gemini rapor başlığı oluşturmadı."
            );
        }

        if (result.summary == null
                || result.summary.isBlank()) {

            throw new IllegalStateException(
                    "Gemini rapor özeti oluşturmadı."
            );
        }

        if (result.estimatedMinimumPrice == null
                || result.estimatedMaximumPrice == null) {

            throw new IllegalStateException(
                    "Gemini fiyat tahmini oluşturmadı."
            );
        }

        if (result.estimatedMinimumPrice.signum() < 0
                || result.estimatedMaximumPrice.signum() < 0) {

            throw new IllegalStateException(
                    "Gemini negatif fiyat döndürdü."
            );
        }

        if (result.estimatedMinimumPrice.compareTo(
                result.estimatedMaximumPrice
        ) > 0) {

            throw new IllegalStateException(
                    "Gemini geçersiz fiyat aralığı döndürdü."
            );
        }

        if (!"TRY".equalsIgnoreCase(
                result.currency
        )) {
            throw new IllegalStateException(
                    "Gemini geçersiz para birimi döndürdü."
            );
        }

        BigDecimal priceDifference =
                result.estimatedMaximumPrice.subtract(
                        result.estimatedMinimumPrice
                );

        if (priceDifference.compareTo(
                BigDecimal.valueOf(10_000)
        ) > 0) {
            result.estimatedMaximumPrice =
                    result.estimatedMinimumPrice.add(
                            BigDecimal.valueOf(10_000)
                    );
        }
    }
}