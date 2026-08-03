package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.client.AiAnalysisClient;
import com.gorkem.vehicle_inspector.dto.response.AiAnalysisResponse;
import com.gorkem.vehicle_inspector.dto.response.BoundingBoxResponse;
import com.gorkem.vehicle_inspector.dto.response.DamageInspectionResponse;
import com.gorkem.vehicle_inspector.dto.response.DetectedObjectResponse;
import com.gorkem.vehicle_inspector.dto.response.RepairRecommendationResponse;
import com.gorkem.vehicle_inspector.entity.DamageDetection;
import com.gorkem.vehicle_inspector.entity.DamageInspection;
import com.gorkem.vehicle_inspector.entity.DamageInspectionPriceDetail;
import com.gorkem.vehicle_inspector.entity.DamageRepairRecommendation;
import com.gorkem.vehicle_inspector.entity.InspectionStatus;
import com.gorkem.vehicle_inspector.entity.RepairPrice;
import com.gorkem.vehicle_inspector.entity.User;
import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import com.gorkem.vehicle_inspector.exception.ResourceNotFoundException;
import com.gorkem.vehicle_inspector.mapper.DamageInspectionMapper;
import com.gorkem.vehicle_inspector.repository.DamageInspectionRepository;
import com.gorkem.vehicle_inspector.repository.UserRepository;
import com.gorkem.vehicle_inspector.repository.VehicleRepository;
import com.gorkem.vehicle_inspector.dto.response.InspectionReportResponse;
import com.gorkem.vehicle_inspector.service.report.InspectionReportProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    private final PriceEstimationService priceEstimationService;
    private final InspectionReportProvider inspectionReportProvider;

    public DamageInspectionService(
            DamageInspectionRepository inspectionRepository,
            VehicleRepository vehicleRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService,
            AiAnalysisClient aiAnalysisClient,
            PriceEstimationService priceEstimationService,
            InspectionReportProvider inspectionReportProvider
    ) {
        this.inspectionRepository = inspectionRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.aiAnalysisClient = aiAnalysisClient;
        this.priceEstimationService = priceEstimationService;
        this.inspectionReportProvider = inspectionReportProvider;
    }

    private DamageInspectionResponse buildResponse(
            DamageInspection inspection
    ) {
        InspectionReportResponse report = null;

        if (inspection.getStatus() == InspectionStatus.COMPLETED) {
            report = inspectionReportProvider.generateReport(
                    inspection
            );
        }

        return DamageInspectionMapper.toResponse(
                inspection,
                report
        );
    }

    @Transactional
    public DamageInspectionResponse createInspection(
            Long vehicleId,
            String authenticatedEmail
    ) {
        User user = findUserByEmail(authenticatedEmail);

        Vehicle vehicle = findVehicleByIdAndUserId(
                vehicleId,
                user.getId()
        );

        DamageInspection inspection =
                new DamageInspection(
                        vehicle,
                        user,
                        InspectionStatus.PENDING
                );

        DamageInspection savedInspection =
                inspectionRepository.save(inspection);

        return buildResponse(
                savedInspection
        );
    }

    @Transactional(readOnly = true)
    public List<DamageInspectionResponse> getMyInspections(
            String authenticatedEmail
    ) {
        User user = findUserByEmail(authenticatedEmail);

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
        User user = findUserByEmail(authenticatedEmail);

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
        User user = findUserByEmail(authenticatedEmail);

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
                fileStorageService.storeImage(image);

        inspection.setImagePath(
                "/uploads/" + storedFilename
        );

        inspection.clearDetections();
        inspection.clearPriceDetails();
        inspection.clearRepairRecommendations();

        inspection.setStatus(InspectionStatus.PENDING);
        inspection.setDamageSeverity(null);
        inspection.setConfidenceScore(null);
        inspection.setAnalysisMessage(null);
        inspection.setCompletedAt(null);

        inspection.setEstimatedMinimumPrice(null);
        inspection.setEstimatedMaximumPrice(null);
        inspection.setPriceCurrency(null);
        inspection.setPriceCalculatedAt(null);
        inspection.setPriceAvailable(false);
        inspection.setPriceMessage(null);

        DamageInspection updatedInspection =
                inspectionRepository.save(inspection);

        return buildResponse(
                updatedInspection
        );
    }

    @Transactional
    public DamageInspectionResponse analyzeInspection(
            Long inspectionId,
            String authenticatedEmail
    ) {
        User user = findUserByEmail(authenticatedEmail);

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

        inspectionRepository.save(inspection);

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
            inspection.clearPriceDetails();
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

            calculateRepairPrices(inspection);

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
                    inspectionRepository.save(inspection);

            return buildResponse(
                    failedInspection
            );
        }

        DamageInspection savedInspection =
                inspectionRepository.save(inspection);

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

            inspection.addDetection(detection);
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

    private void calculateRepairPrices(
            DamageInspection inspection
    ) {
        List<DamageRepairRecommendation>
                repairRecommendations =
                inspection.getRepairRecommendations();

        List<RepairPrice> matchingPrices =
                priceEstimationService.findMatchingPrices(
                        inspection.getVehicle(),
                        repairRecommendations,
                        inspection.getDamageSeverity()
                );

        int totalPriceConfigurationCount = 0;

        for (DamageRepairRecommendation recommendation
                : repairRecommendations) {

            if (recommendation == null
                    || recommendation.getDamageType() == null
                    || recommendation.getRecommendedAction() == null
                    || recommendation.getAffectedParts() == null) {
                continue;
            }

            for (VehiclePart affectedPart
                    : recommendation.getAffectedParts()) {

                if (affectedPart == null
                        || affectedPart
                        == VehiclePart.UNKNOWN) {
                    continue;
                }

                totalPriceConfigurationCount++;

                RepairPrice matchingPrice =
                        findMatchingPrice(
                                matchingPrices,
                                affectedPart,
                                recommendation
                                        .getRecommendedAction()
                        );

                DamageInspectionPriceDetail priceDetail =
                        createPriceDetail(
                                inspection,
                                recommendation,
                                affectedPart,
                                matchingPrice
                        );

                inspection.addPriceDetail(
                        priceDetail
                );
            }
        }

        BigDecimal totalMinimumPrice =
                matchingPrices.stream()
                        .map(
                                RepairPrice::getMinimumPrice
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalMaximumPrice =
                matchingPrices.stream()
                        .map(
                                RepairPrice::getMaximumPrice
                        )
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        inspection.setPriceCurrency("TRY");
        inspection.setPriceCalculatedAt(
                LocalDateTime.now()
        );

        if (matchingPrices.isEmpty()) {
            inspection.setEstimatedMinimumPrice(null);
            inspection.setEstimatedMaximumPrice(null);
            inspection.setPriceAvailable(false);
            inspection.setPriceMessage(
                    "Önerilen onarım işlemleri için uygun fiyat kaydı bulunamadı."
            );
            return;
        }

        inspection.setEstimatedMinimumPrice(
                totalMinimumPrice
        );

        inspection.setEstimatedMaximumPrice(
                totalMaximumPrice
        );

        inspection.setPriceAvailable(true);

        if (matchingPrices.size()
                == totalPriceConfigurationCount) {
            inspection.setPriceMessage(
                    "Tüm önerilen onarım işlemleri için tahmini fiyat hesaplandı."
            );
        } else {
            inspection.setPriceMessage(
                    matchingPrices.size()
                            + " / "
                            + totalPriceConfigurationCount
                            + " onarım işlemi için fiyat bulundu."
            );
        }
    }

    private RepairPrice findMatchingPrice(
            List<RepairPrice> matchingPrices,
            VehiclePart vehiclePart,
            com.gorkem.vehicle_inspector.entity.RepairAction
                    repairAction
    ) {
        return matchingPrices.stream()
                .filter(price ->
                        price.getVehiclePart()
                                == vehiclePart
                )
                .filter(price ->
                        price.getRepairAction()
                                == repairAction
                )
                .findFirst()
                .orElse(null);
    }

    private DamageInspectionPriceDetail createPriceDetail(
            DamageInspection inspection,
            DamageRepairRecommendation recommendation,
            VehiclePart affectedPart,
            RepairPrice matchingPrice
    ) {
        if (matchingPrice == null) {
            return new DamageInspectionPriceDetail(
                    inspection,
                    affectedPart,
                    recommendation.getDamageType(),
                    recommendation.getRecommendedAction(),
                    false,
                    null,
                    null
            );
        }

        return new DamageInspectionPriceDetail(
                inspection,
                affectedPart,
                recommendation.getDamageType(),
                recommendation.getRecommendedAction(),
                true,
                matchingPrice.getMinimumPrice(),
                matchingPrice.getMaximumPrice()
        );
    }

    private User findUserByEmail(String email) {
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