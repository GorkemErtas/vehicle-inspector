package com.gorkem.vehicle_inspector.client;

import com.gorkem.vehicle_inspector.dto.response.AiAnalysisResponse;
import com.gorkem.vehicle_inspector.exception.AiServiceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class AiAnalysisClient {

    private final RestTemplate restTemplate;
    private final String aiServiceBaseUrl;

    public AiAnalysisClient(
            RestTemplate restTemplate,
            @Value("${application.ai-service.base-url}")
            String aiServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.aiServiceBaseUrl = aiServiceBaseUrl;
    }

    public AiAnalysisResponse analyze(Path imagePath) {

        if (!Files.exists(imagePath)) {
            throw new AiServiceException(
                    "Analiz edilecek fotoğraf bulunamadı."
            );
        }

        FileSystemResource imageResource =
                new FileSystemResource(imagePath);

        HttpHeaders imageHeaders = new HttpHeaders();

        imageHeaders.setContentDisposition(
                ContentDisposition
                        .formData()
                        .name("image")
                        .filename(imagePath.getFileName().toString())
                        .build()
        );

        HttpEntity<FileSystemResource> imagePart =
                new HttpEntity<>(
                        imageResource,
                        imageHeaders
                );

        MultiValueMap<String, Object> multipartBody =
                new LinkedMultiValueMap<>();

        multipartBody.add("image", imagePart);

        HttpHeaders requestHeaders = new HttpHeaders();

        requestHeaders.setContentType(
                MediaType.MULTIPART_FORM_DATA
        );

        HttpEntity<MultiValueMap<String, Object>> request =
                new HttpEntity<>(
                        multipartBody,
                        requestHeaders
                );

        try {
            ResponseEntity<AiAnalysisResponse> response =
                    restTemplate.postForEntity(
                            aiServiceBaseUrl + "/api/v1/analyze",
                            request,
                            AiAnalysisResponse.class
                    );

            AiAnalysisResponse responseBody =
                    response.getBody();

            if (responseBody == null) {
                throw new AiServiceException(
                        "AI servisi boş cevap döndürdü."
                );
            }

            return responseBody;

        } catch (RestClientException exception) {
            throw new AiServiceException(
                    "AI analiz servisine ulaşılamadı.",
                    exception
            );
        }
    }
}