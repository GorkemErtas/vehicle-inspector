package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.dto.response.DamageInspectionResponse;
import com.gorkem.vehicle_inspector.entity.DamageInspection;
import com.gorkem.vehicle_inspector.entity.InspectionStatus;
import com.gorkem.vehicle_inspector.entity.User;
import com.gorkem.vehicle_inspector.entity.Vehicle;
import com.gorkem.vehicle_inspector.exception.ResourceNotFoundException;
import com.gorkem.vehicle_inspector.mapper.DamageInspectionMapper;
import com.gorkem.vehicle_inspector.repository.DamageInspectionRepository;
import com.gorkem.vehicle_inspector.repository.UserRepository;
import com.gorkem.vehicle_inspector.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.gorkem.vehicle_inspector.client.AiAnalysisClient;
import com.gorkem.vehicle_inspector.dto.response.AiAnalysisResponse;
import com.gorkem.vehicle_inspector.exception.AiServiceException;
import com.gorkem.vehicle_inspector.dto.response.BoundingBoxResponse;
import com.gorkem.vehicle_inspector.dto.response.DetectedObjectResponse;
import com.gorkem.vehicle_inspector.entity.DamageDetection;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class DamageInspectionService {

    private final DamageInspectionRepository inspectionRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final AiAnalysisClient aiAnalysisClient;
    private final PriceEstimationService priceEstimationService;

    public DamageInspectionService(
            DamageInspectionRepository inspectionRepository,
            VehicleRepository vehicleRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService,
            AiAnalysisClient aiAnalysisClient,
            PriceEstimationService priceEstimationService
    ) {
        this.inspectionRepository = inspectionRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.aiAnalysisClient = aiAnalysisClient;
        this.priceEstimationService = priceEstimationService;
    }

    @Transactional
    public DamageInspectionResponse createInspection(
            Long vehicleId,
            String authenticatedEmail
    ) {
        User user = findUserByEmail(authenticatedEmail);
        Vehicle vehicle =
                findVehicleByIdAndUserId(
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

        return DamageInspectionMapper.toResponse(
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
                .map(DamageInspectionMapper::toResponse)
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

        return DamageInspectionMapper.toResponse(inspection);
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

        inspection.setStatus(InspectionStatus.PENDING);

        inspection.setDamageType(null);
        inspection.setDamageSeverity(null);
        inspection.setVehiclePart(null);
        inspection.setRecommendedAction(null);
        inspection.setPartReplacementRequired(null);
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

        return DamageInspectionMapper.toResponse(
                updatedInspection
        );
    }

    private User findUserByEmail(String email) {
        return userRepository
                .findByEmail(email.trim().toLowerCase())
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
                .findByIdAndUserId(vehicleId, userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Araç bulunamadı. ID: "
                                        + vehicleId
                        )
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

        inspection.setStatus(InspectionStatus.PROCESSING);
        inspectionRepository.save(inspection);

        try {
            Path storedImagePath =
                    fileStorageService.resolveStoredFile(
                            inspection.getImagePath()
                    );

            AiAnalysisResponse aiResponse =
                    aiAnalysisClient.analyze(storedImagePath);

            inspection.clearDetections();

            if (aiResponse.getDetections() != null) {
                for (DetectedObjectResponse detectedObject
                        : aiResponse.getDetections()) {

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
                                    boundingBox.getX1(),
                                    boundingBox.getY1(),
                                    boundingBox.getX2(),
                                    boundingBox.getY2()
                            );

                    inspection.addDetection(detection);
                }
            }

            inspection.setDamageType(
                    aiResponse.getDamageType()
            );
            inspection.setDamageSeverity(
                    aiResponse.getDamageSeverity()
            );
            inspection.setVehiclePart(
                    aiResponse.getVehiclePart()
            );
            inspection.setRecommendedAction(
                    aiResponse.getRecommendedAction()
            );
            inspection.setPartReplacementRequired(
                    aiResponse.getPartReplacementRequired()
            );
            inspection.setConfidenceScore(
                    aiResponse.getConfidenceScore()
            );
            inspection.setAnalysisMessage(
                    aiResponse.getAnalysisMessage()
            );

            priceEstimationService
                    .findMatchingPrice(
                            inspection.getVehicle(),
                            inspection.getVehiclePart(),
                            inspection.getRecommendedAction(),
                            inspection.getDamageSeverity()
                    )
                    .ifPresentOrElse(
                            repairPrice -> {
                                inspection.setEstimatedMinimumPrice(
                                        repairPrice.getMinimumPrice()
                                );
                                inspection.setEstimatedMaximumPrice(
                                        repairPrice.getMaximumPrice()
                                );
                                inspection.setPriceCurrency("TRY");
                                inspection.setPriceCalculatedAt(
                                        LocalDateTime.now()
                                );
                                inspection.setPriceAvailable(true);
                                inspection.setPriceMessage(
                                        "Tahmini onarım fiyatı başarıyla hesaplandı."
                                );
                            },
                            () -> {
                                inspection.setEstimatedMinimumPrice(null);
                                inspection.setEstimatedMaximumPrice(null);
                                inspection.setPriceCurrency("TRY");
                                inspection.setPriceCalculatedAt(
                                        LocalDateTime.now()
                                );
                                inspection.setPriceAvailable(false);
                                inspection.setPriceMessage(
                                        "Bu araç, parça, işlem ve hasar derecesi için güncel fiyat kaydı bulunamadı."
                                );
                            }
                    );

            inspection.setStatus(
                    InspectionStatus.COMPLETED
            );
            inspection.setCompletedAt(
                    LocalDateTime.now()
            );

        } catch (RuntimeException exception) {

            inspection.setStatus(InspectionStatus.FAILED);
            inspection.setAnalysisMessage(
                    exception.getMessage()
            );

            DamageInspection failedInspection =
                    inspectionRepository.save(inspection);

            return DamageInspectionMapper.toResponse(failedInspection);
        }

        DamageInspection savedInspection =
                inspectionRepository.save(inspection);

        return DamageInspectionMapper.toResponse(
                savedInspection
        );
    }
}