package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.client.AiAnalysisClient;
import com.gorkem.vehicle_inspector.dto.response.AiAnalysisResponse;
import com.gorkem.vehicle_inspector.dto.response.BoundingBoxResponse;
import com.gorkem.vehicle_inspector.dto.response.DamageInspectionResponse;
import com.gorkem.vehicle_inspector.dto.response.DetectedObjectResponse;
import com.gorkem.vehicle_inspector.dto.response.InspectionReportResponse;
import com.gorkem.vehicle_inspector.dto.response.RepairRecommendationResponse;
import com.gorkem.vehicle_inspector.entity.DamageDetection;
import com.gorkem.vehicle_inspector.entity.DamageInspection;
import com.gorkem.vehicle_inspector.entity.DamageRepairRecommendation;
import com.gorkem.vehicle_inspector.entity.InspectionStatus;
import com.gorkem.vehicle_inspector.entity.User;
import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import com.gorkem.vehicle_inspector.exception.ResourceNotFoundException;
import com.gorkem.vehicle_inspector.mapper.DamageInspectionMapper;
import com.gorkem.vehicle_inspector.repository.DamageInspectionRepository;
import com.gorkem.vehicle_inspector.repository.UserRepository;
import com.gorkem.vehicle_inspector.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.gorkem.vehicle_inspector.mapper.InspectionReportMapper;
import com.gorkem.vehicle_inspector.entity.InspectionReport;
import com.gorkem.vehicle_inspector.service.report.GeminiInspectionReportService;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class DamageInspectionService {

    private final DamageInspectionRepository inspectionRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AiAnalysisClient aiAnalysisClient;
    private final GeminiInspectionReportService geminiInspectionReportService;

    public DamageInspectionService(
            DamageInspectionRepository inspectionRepository,
            VehicleRepository vehicleRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService,
            AiAnalysisClient aiAnalysisClient,
            GeminiInspectionReportService geminiInspectionReportService
    ) {
        this.inspectionRepository = inspectionRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.aiAnalysisClient = aiAnalysisClient;
        this.geminiInspectionReportService = geminiInspectionReportService;
    }

    private DamageInspectionResponse buildResponse(
            DamageInspection inspection
    ) {
        InspectionReportResponse report =
                InspectionReportMapper.toResponse(
                        inspection.getReport()
                );

        return DamageInspectionMapper.toResponse(
                inspection,
                report
        );
    }

    @Transactional
    public DamageInspectionResponse createInspection(
            Long vehicleId,
            String city,
            String authenticatedEmail
    ) {
        User user = findUserByEmail(
                authenticatedEmail
        );

        Vehicle vehicle = findVehicleByIdAndUserId(
                vehicleId,
                user.getId()
        );

        if (city == null || city.isBlank()) {
            throw new IllegalArgumentException(
                    "Şehir bilgisi zorunludur."
            );
        }

        DamageInspection inspection =
                new DamageInspection(
                        vehicle,
                        user,
                        InspectionStatus.PENDING
                );

        inspection.setLocationCity(
                normalizeCity(city)
        );

        DamageInspection savedInspection =
                inspectionRepository.save(
                        inspection
                );

        return buildResponse(
                savedInspection
        );
    }

    private String normalizeCity(
            String city
    ) {
        String normalized =
                city.trim();

        if (normalized.length() > 100) {
            throw new IllegalArgumentException(
                    "Şehir adı en fazla 100 karakter olabilir."
            );
        }

        return normalized;
    }

    @Transactional(readOnly = true)
    public List<DamageInspectionResponse> getMyInspections(
            String authenticatedEmail
    ) {
        User user = findUserByEmail(
                authenticatedEmail
        );

        return inspectionRepository
                .findAllByUserIdOrderByCreatedAtDesc(
                        user.getId()
                )
                .stream()
                .map(this::buildResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public DamageInspectionResponse getMyInspectionById(
            Long inspectionId,
            String authenticatedEmail
    ) {
        User user = findUserByEmail(
                authenticatedEmail
        );

        DamageInspection inspection =
                inspectionRepository
                        .findByIdAndUserId(
                                inspectionId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Hasar incelemesi bulunamadı. ID: "
                                                + inspectionId
                                )
                        );

        return buildResponse(
                inspection
        );
    }

    @Transactional
    public DamageInspectionResponse uploadInspectionImage(
            Long inspectionId,
            MultipartFile image,
            String authenticatedEmail
    ) {
        User user = findUserByEmail(
                authenticatedEmail
        );

        DamageInspection inspection =
                inspectionRepository
                        .findByIdAndUserId(
                                inspectionId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Hasar incelemesi bulunamadı. ID: "
                                                + inspectionId
                                )
                        );

        String storedFilename =
                fileStorageService.storeImage(
                        image
                );

        inspection.setImagePath(
                "/uploads/" + storedFilename
        );

        inspection.clearDetections();
        inspection.clearRepairRecommendations();
        inspection.setReport(null);

        inspection.setStatus(
                InspectionStatus.PENDING
        );

        inspection.setDamageSeverity(null);
        inspection.setConfidenceScore(null);
        inspection.setAnalysisMessage(null);
        inspection.setCompletedAt(null);

        DamageInspection updatedInspection =
                inspectionRepository.save(
                        inspection
                );

        return buildResponse(
                updatedInspection
        );
    }

    @Transactional
    public DamageInspectionResponse analyzeInspection(
            Long inspectionId,
            String authenticatedEmail
    ) {
        User user = findUserByEmail(
                authenticatedEmail
        );

        DamageInspection inspection =
                inspectionRepository
                        .findByIdAndUserId(
                                inspectionId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Hasar incelemesi bulunamadı. ID: "
                                                + inspectionId
                                )
                        );

        if (inspection.getImagePath() == null
                || inspection.getImagePath().isBlank()) {

            throw new IllegalStateException(
                    "Analizden önce fotoğraf yüklenmelidir."
            );
        }

        inspection.setStatus(
                InspectionStatus.PROCESSING
        );

        inspectionRepository.save(
                inspection
        );

        try {
            Path storedImagePath =
                    fileStorageService.resolveStoredFile(
                            inspection.getImagePath()
                    );

            AiAnalysisResponse aiResponse =
                    aiAnalysisClient.analyze(
                            storedImagePath
                    );

            inspection.clearDetections();
            inspection.clearRepairRecommendations();

            saveDetections(
                    inspection,
                    aiResponse.getDetections()
            );

            saveRepairRecommendations(
                    inspection,
                    aiResponse.getRepairRecommendations()
            );

            inspection.setDamageSeverity(
                    aiResponse.getDamageSeverity()
            );

            inspection.setConfidenceScore(
                    aiResponse.getConfidenceScore()
            );

            inspection.setAnalysisMessage(
                    aiResponse.getAnalysisMessage()
            );

            try {
                InspectionReport report =
                        geminiInspectionReportService.generateReport(
                                inspection
                        );

                inspection.setReport(report);

            } catch (RuntimeException exception) {

                System.err.println(
                        "GEMINI ERROR: "
                                + exception.getMessage()
                );

                exception.printStackTrace();

                inspection.setReport(null);
            }

            inspection.setStatus(
                    InspectionStatus.COMPLETED
            );

            inspection.setCompletedAt(
                    LocalDateTime.now()
            );

        } catch (RuntimeException exception) {

            inspection.setStatus(
                    InspectionStatus.FAILED
            );

            inspection.setAnalysisMessage(
                    exception.getMessage()
            );

            DamageInspection failedInspection =
                    inspectionRepository.save(
                            inspection
                    );

            return buildResponse(
                    failedInspection
            );
        }

        DamageInspection savedInspection =
                inspectionRepository.save(
                        inspection
                );

        return buildResponse(
                savedInspection
        );
    }

    private void saveDetections(
            DamageInspection inspection,
            List<DetectedObjectResponse> detections
    ) {
        if (detections == null) {
            return;
        }

        for (DetectedObjectResponse detectedObject
                : detections) {

            if (detectedObject == null
                    || detectedObject.getBoundingBox() == null) {
                continue;
            }

            BoundingBoxResponse boundingBox =
                    detectedObject.getBoundingBox();

            if (boundingBox.getX1() == null
                    || boundingBox.getY1() == null
                    || boundingBox.getX2() == null
                    || boundingBox.getY2() == null) {
                continue;
            }

            DamageDetection detection =
                    new DamageDetection(
                            inspection,
                            detectedObject.getLabel(),
                            detectedObject.getConfidence(),
                            detectedObject.getAffectedPart(),
                            boundingBox.getX1(),
                            boundingBox.getY1(),
                            boundingBox.getX2(),
                            boundingBox.getY2()
                    );

            inspection.addDetection(
                    detection
            );
        }
    }

    private void saveRepairRecommendations(
            DamageInspection inspection,
            List<RepairRecommendationResponse>
                    recommendationResponses
    ) {
        if (recommendationResponses == null) {
            return;
        }

        for (RepairRecommendationResponse response
                : recommendationResponses) {

            if (response == null
                    || response.getDamageType() == null
                    || response.getRecommendedAction() == null) {
                continue;
            }

            Set<VehiclePart> recommendationParts =
                    new LinkedHashSet<>();

            if (response.getAffectedParts() != null) {

                response.getAffectedParts()
                        .stream()
                        .filter(Objects::nonNull)
                        .filter(part ->
                                part != VehiclePart.UNKNOWN
                        )
                        .forEach(
                                recommendationParts::add
                        );
            }

            DamageRepairRecommendation recommendation =
                    new DamageRepairRecommendation(
                            inspection,
                            response.getDamageType(),
                            response.getRecommendedAction(),
                            Boolean.TRUE.equals(
                                    response
                                            .getPartReplacementRequired()
                            ),
                            recommendationParts
                    );

            inspection.addRepairRecommendation(
                    recommendation
            );
        }
    }

    private User findUserByEmail(
            String email
    ) {
        return userRepository
                .findByEmail(
                        email.trim().toLowerCase()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Kullanıcı bulunamadı."
                        )
                );
    }

    private Vehicle findVehicleByIdAndUserId(
            Long vehicleId,
            Long userId
    ) {
        return vehicleRepository
                .findByIdAndUserId(
                        vehicleId,
                        userId
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Araç bulunamadı. ID: "
                                        + vehicleId
                        )
                );
    }
}