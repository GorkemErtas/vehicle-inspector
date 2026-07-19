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

import java.util.List;

@Service
public class DamageInspectionService {

    private final DamageInspectionRepository inspectionRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;

    public DamageInspectionService(
            DamageInspectionRepository inspectionRepository,
            VehicleRepository vehicleRepository,
            UserRepository userRepository
    ) {
        this.inspectionRepository = inspectionRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
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
}