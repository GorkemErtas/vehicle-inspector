package com.gorkem.vehicle_inspector.service;

import com.gorkem.vehicle_inspector.dto.request.RepairPriceRequest;
import com.gorkem.vehicle_inspector.dto.response.RepairPriceResponse;
import com.gorkem.vehicle_inspector.entity.RepairPrice;
import com.gorkem.vehicle_inspector.exception.DuplicateResourceException;
import com.gorkem.vehicle_inspector.exception.ResourceNotFoundException;
import com.gorkem.vehicle_inspector.mapper.RepairPriceMapper;
import com.gorkem.vehicle_inspector.repository.RepairPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RepairPriceService {

    private final RepairPriceRepository repairPriceRepository;

    public RepairPriceService(
            RepairPriceRepository repairPriceRepository
    ) {
        this.repairPriceRepository = repairPriceRepository;
    }

    @Transactional
    public RepairPriceResponse createRepairPrice(
            RepairPriceRequest request
    ) {
        validatePriceRange(
                request.minimumPrice(),
                request.maximumPrice()
        );

        validateDuplicateForCreate(request);

        RepairPrice repairPrice =
                RepairPriceMapper.toEntity(request);

        RepairPrice savedRepairPrice =
                repairPriceRepository.save(repairPrice);

        return RepairPriceMapper.toResponse(savedRepairPrice);
    }

    @Transactional(readOnly = true)
    public List<RepairPriceResponse> getAllRepairPrices() {
        return repairPriceRepository
                .findAllByOrderByBrandAscModelAscModelYearDesc()
                .stream()
                .map(RepairPriceMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<RepairPriceResponse> getActiveRepairPrices() {
        return repairPriceRepository
                .findAllByActiveTrueOrderByBrandAscModelAsc()
                .stream()
                .map(RepairPriceMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RepairPriceResponse getRepairPriceById(Long id) {
        RepairPrice repairPrice =
                findRepairPriceById(id);

        return RepairPriceMapper.toResponse(repairPrice);
    }

    @Transactional
    public RepairPriceResponse updateRepairPrice(
            Long id,
            RepairPriceRequest request
    ) {
        validatePriceRange(
                request.minimumPrice(),
                request.maximumPrice()
        );

        RepairPrice repairPrice =
                findRepairPriceById(id);

        validateDuplicateForUpdate(id, request);

        RepairPriceMapper.updateEntity(
                repairPrice,
                request
        );

        RepairPrice updatedRepairPrice =
                repairPriceRepository.save(repairPrice);

        return RepairPriceMapper.toResponse(
                updatedRepairPrice
        );
    }

    @Transactional
    public RepairPriceResponse updateActiveStatus(
            Long id,
            boolean active
    ) {
        RepairPrice repairPrice =
                findRepairPriceById(id);

        repairPrice.setActive(active);

        RepairPrice updatedRepairPrice =
                repairPriceRepository.save(repairPrice);

        return RepairPriceMapper.toResponse(
                updatedRepairPrice
        );
    }

    private RepairPrice findRepairPriceById(Long id) {
        return repairPriceRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Onarım fiyatı bulunamadı. ID: "
                                        + id
                        )
                );
    }

    private void validatePriceRange(
            BigDecimal minimumPrice,
            BigDecimal maximumPrice
    ) {
        if (minimumPrice.compareTo(maximumPrice) > 0) {
            throw new IllegalStateException(
                    "Minimum fiyat maksimum fiyattan büyük olamaz."
            );
        }
    }

    private void validateDuplicateForCreate(
            RepairPriceRequest request
    ) {
        boolean exists =
                repairPriceRepository
                        .existsByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairActionAndDamageSeverity(
                                normalizeText(request.brand()),
                                normalizeText(request.model()),
                                request.modelYear(),
                                request.vehiclePart(),
                                request.repairAction(),
                                request.damageSeverity()
                        );

        if (exists) {
            throw new DuplicateResourceException(
                    "Bu araç, parça, işlem ve hasar derecesi için fiyat kaydı zaten mevcut."
            );
        }
    }

    private void validateDuplicateForUpdate(
            Long id,
            RepairPriceRequest request
    ) {
        boolean exists =
                repairPriceRepository
                        .existsByBrandIgnoreCaseAndModelIgnoreCaseAndModelYearAndVehiclePartAndRepairActionAndDamageSeverityAndIdNot(
                                normalizeText(request.brand()),
                                normalizeText(request.model()),
                                request.modelYear(),
                                request.vehiclePart(),
                                request.repairAction(),
                                request.damageSeverity(),
                                id
                        );

        if (exists) {
            throw new DuplicateResourceException(
                    "Bu araç, parça, işlem ve hasar derecesi için başka bir fiyat kaydı zaten mevcut."
            );
        }
    }

    private String normalizeText(String value) {
        return value.trim();
    }
}