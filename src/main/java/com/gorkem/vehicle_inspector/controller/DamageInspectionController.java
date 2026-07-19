package com.gorkem.vehicle_inspector.controller;

import com.gorkem.vehicle_inspector.dto.response.DamageInspectionResponse;
import com.gorkem.vehicle_inspector.service.DamageInspectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(
            value = "/{inspectionId}/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DamageInspectionResponse>
    uploadInspectionImage(
            @PathVariable Long inspectionId,
            @RequestPart("image") MultipartFile image,
            Authentication authentication
    ) {
        DamageInspectionResponse response =
                inspectionService.uploadInspectionImage(
                        inspectionId,
                        image,
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }
}