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

    public DamageInspectionService(
            DamageInspectionRepository inspectionRepository,
            VehicleRepository vehicleRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService,
            AiAnalysisClient aiAnalysisClient
    ) {
        this.inspectionRepository = inspectionRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
        this.aiAnalysisClient = aiAnalysisClient;
    }

    @Transactional
    public DamageInspectionResponse createInspection(
            Long vehicleId,
            String authenticatedEmail
    ) {
        User user = findUserByEmail(authenticatedEmail);
        Vehicle vehicle = findVehicleById(vehicleId);

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

    private Vehicle findVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
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