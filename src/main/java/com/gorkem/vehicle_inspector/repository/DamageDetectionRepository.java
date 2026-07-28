package com.gorkem.vehicle_inspector.repository;

import com.gorkem.vehicle_inspector.entity.DamageDetection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DamageDetectionRepository
        extends JpaRepository<DamageDetection, Long> {

    List<DamageDetection> findAllByInspectionId(
            Long inspectionId
    );

    void deleteAllByInspectionId(
            Long inspectionId
    );
}