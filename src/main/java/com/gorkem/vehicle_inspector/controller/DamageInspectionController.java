package com.gorkem.vehicle_inspector.controller;

import com.gorkem.vehicle_inspector.dto.response.DamageInspectionResponse;
import com.gorkem.vehicle_inspector.service.DamageInspectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inspections")
public class DamageInspectionController {

    private final DamageInspectionService inspectionService;

    public DamageInspectionController(
            DamageInspectionService inspectionService
    ) {
        this.inspectionService = inspectionService;
    }

    @PostMapping
    public ResponseEntity<DamageInspectionResponse>
    createInspection(
            @RequestParam Long vehicleId,
            Authentication authentication
    ) {
        DamageInspectionResponse response =
                inspectionService.createInspection(
                        vehicleId,
                        authentication.getName()
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<DamageInspectionResponse>>
    getMyInspections(Authentication authentication) {

        return ResponseEntity.ok(
                inspectionService.getMyInspections(
                        authentication.getName()
                )
        );
    }

    @GetMapping("/{inspectionId}")
    public ResponseEntity<DamageInspectionResponse>
    getMyInspectionById(
            @PathVariable Long inspectionId,
            Authentication authentication
    ) {
        return ResponseEntity.ok(
                inspectionService.getMyInspectionById(
                        inspectionId,
                        authentication.getName()
                )
        );
    }
}