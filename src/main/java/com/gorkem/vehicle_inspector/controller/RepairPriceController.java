package com.gorkem.vehicle_inspector.controller;

import com.gorkem.vehicle_inspector.dto.request.RepairPriceRequest;
import com.gorkem.vehicle_inspector.dto.response.RepairPriceResponse;
import com.gorkem.vehicle_inspector.service.RepairPriceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/repair-prices")
public class RepairPriceController {

    private final RepairPriceService repairPriceService;

    public RepairPriceController(
            RepairPriceService repairPriceService
    ) {
        this.repairPriceService = repairPriceService;
    }

    @PostMapping
    public ResponseEntity<RepairPriceResponse> createRepairPrice(
            @Valid @RequestBody RepairPriceRequest request
    ) {

        RepairPriceResponse response =
                repairPriceService.createRepairPrice(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<RepairPriceResponse>> getAllRepairPrices() {

        return ResponseEntity.ok(
                repairPriceService.getAllRepairPrices()
        );
    }

    @GetMapping("/active")
    public ResponseEntity<List<RepairPriceResponse>> getActiveRepairPrices() {

        return ResponseEntity.ok(
                repairPriceService.getActiveRepairPrices()
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<RepairPriceResponse> getRepairPriceById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                repairPriceService.getRepairPriceById(id)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<RepairPriceResponse> updateRepairPrice(
            @PathVariable Long id,
            @Valid @RequestBody RepairPriceRequest request
    ) {

        return ResponseEntity.ok(
                repairPriceService.updateRepairPrice(
                        id,
                        request
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<RepairPriceResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam boolean active
    ) {

        return ResponseEntity.ok(
                repairPriceService.updateActiveStatus(
                        id,
                        active
                )
        );
    }
}