package com.gorkem.vehicle_inspector.service.report;

import com.gorkem.vehicle_inspector.dto.response.InspectionReportResponse;
import com.gorkem.vehicle_inspector.entity.DamageInspection;
import com.gorkem.vehicle_inspector.entity.DamageRepairRecommendation;
import com.gorkem.vehicle_inspector.entity.DamageSeverity;
import com.gorkem.vehicle_inspector.entity.DamageType;
import com.gorkem.vehicle_inspector.entity.RepairAction;
import com.gorkem.vehicle_inspector.entity.VehiclePart;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RuleBasedInspectionReportProvider
        implements InspectionReportProvider {

    private static final Locale TURKISH_LOCALE =
            Locale.forLanguageTag("tr-TR");

    @Override
    public InspectionReportResponse generateReport(
            DamageInspection inspection
    ) {
        if (inspection == null) {
            throw new IllegalArgumentException(
                    "Rapor oluşturmak için inspection gereklidir."
            );
        }

        String title = buildTitle(inspection);

        String summary = buildSummary(inspection);

        String damageDescription =
                buildDamageDescription(inspection);

        String repairRecommendation =
                buildRepairRecommendation(inspection);

        String priceInformation =
                buildPriceInformation(inspection);

        String disclaimer =
                "Kesin değerlendirme için servis incelemesi önerilir.";

        return new InspectionReportResponse(
                title,
                summary,
                damageDescription,
                repairRecommendation,
                priceInformation,
                disclaimer
        );
    }

    private String buildTitle(
            DamageInspection inspection
    ) {
        return translateSeverity(
                inspection.getDamageSeverity()
        ) + " Seviyeli Hasar";
    }

    private String buildSummary(
            DamageInspection inspection
    ) {
        String severityText =
                translateSeverity(
                        inspection.getDamageSeverity()
                ).toLowerCase(TURKISH_LOCALE);

        return "Araçta "
                + severityText
                + " seviyede hasar tespit edildi.";
    }

    private String buildDamageDescription(
            DamageInspection inspection
    ) {
        List<DamageRepairRecommendation> recommendations =
                inspection.getRepairRecommendations();

        if (recommendations == null
                || recommendations.isEmpty()) {
            return "Güvenilir bir hasar türü veya etkilenen "
                    + "araç parçası belirlenememiştir.";
        }

        String damageTypes = recommendations.stream()
                .map(
                        DamageRepairRecommendation::getDamageType
                )
                .filter(Objects::nonNull)
                .distinct()
                .map(this::translateDamageType)
                .collect(
                        Collectors.joining(", ")
                );

        String affectedParts = recommendations.stream()
                .filter(Objects::nonNull)
                .flatMap(recommendation ->
                        recommendation
                                .getAffectedParts()
                                .stream()
                )
                .filter(Objects::nonNull)
                .filter(part ->
                        part != VehiclePart.UNKNOWN
                )
                .distinct()
                .map(this::translateVehiclePart)
                .collect(
                        Collectors.joining(", ")
                );

        if (affectedParts.isBlank()) {
            return capitalize(damageTypes)
                    + " hasarı tespit edildi.";
        }

        return capitalize(damageTypes)
                + " hasarı "
                + affectedParts
                + " bölgesinde tespit edildi.";
    }

    private String buildRepairRecommendation(
            DamageInspection inspection
    ) {
        List<DamageRepairRecommendation> recommendations =
                inspection.getRepairRecommendations();

        if (recommendations == null
                || recommendations.isEmpty()) {
            return "Onarım önerisi oluşturulamadı.";
        }

        String actions = recommendations.stream()
                .map(
                        DamageRepairRecommendation::getRecommendedAction
                )
                .filter(Objects::nonNull)
                .distinct()
                .map(this::translateRepairAction)
                .collect(
                        Collectors.joining(" ve ")
                );

        if (actions.isBlank()) {
            return "Onarım önerisi oluşturulamadı.";
        }

        return capitalize(actions) + " önerilir.";
    }

    private String buildPriceInformation(
            DamageInspection inspection
    ) {
        if (!Boolean.TRUE.equals(
                inspection.getPriceAvailable()
        )) {
            return "Tahmini fiyat bulunamadı.";
        }

        BigDecimal minimumPrice =
                inspection.getEstimatedMinimumPrice();

        BigDecimal maximumPrice =
                inspection.getEstimatedMaximumPrice();

        if (minimumPrice == null
                || maximumPrice == null) {
            return "Tahmini onarım maliyeti hesaplanamamıştır.";
        }

        String currency =
                inspection.getPriceCurrency() == null
                        ? "TRY"
                        : inspection.getPriceCurrency();

        return formatPrice(minimumPrice)
                + " - "
                + formatPrice(maximumPrice)
                + " "
                + currency;
    }

    private String buildVehicleText(
            DamageInspection inspection
    ) {
        if (inspection.getVehicle() == null) {
            return "Araç";
        }

        String brand =
                inspection.getVehicle().getBrand();

        String model =
                inspection.getVehicle().getModel();

        Integer modelYear =
                inspection.getVehicle().getModelYear();

        return String.join(
                " ",
                modelYear == null
                        ? ""
                        : modelYear.toString(),
                brand == null
                        ? ""
                        : brand.trim(),
                model == null
                        ? ""
                        : model.trim()
        ).trim();
    }

    private String translateSeverity(
            DamageSeverity severity
    ) {
        if (severity == null) {
            return "Belirsiz";
        }

        return switch (severity) {
            case NONE -> "Hasarsız";
            case MINOR -> "Hafif";
            case MODERATE -> "Orta";
            case SEVERE -> "Ağır";
            default -> "Belirsiz";
        };
    }

    private String translateDamageType(
            DamageType damageType
    ) {
        if (damageType == null) {
            return "belirsiz hasar";
        }

        return switch (damageType) {
            case SCRATCH -> "çizik";
            case DENT -> "göçük";
            case BROKEN_PART -> "kırık parça";
            default -> damageType
                    .name()
                    .toLowerCase(TURKISH_LOCALE)
                    .replace("_", " ");
        };
    }

    private String translateRepairAction(
            RepairAction repairAction
    ) {
        if (repairAction == null) {
            return "uzman incelemesi";
        }

        return switch (repairAction) {
            case PAINT_TOUCH_UP -> "boya rötuşu";
            case DENT_REPAIR -> "göçük onarımı";
            case PART_REPLACEMENT -> "parça değişimi";
            case PART_REPAIR -> "parça onarımı";
            case FULL_PAINTING -> "tam boyama";
            case NO_ACTION -> "işlem yapılmaması";
            default -> repairAction
                    .name()
                    .toLowerCase(TURKISH_LOCALE)
                    .replace("_", " ");
        };
    }

    private String translateVehiclePart(
            VehiclePart vehiclePart
    ) {
        if (vehiclePart == null) {
            return "belirsiz parça";
        }

        return switch (vehiclePart) {
            case FRONT_DOOR -> "ön kapı";
            case REAR_DOOR -> "arka kapı";
            case FRONT_BUMPER -> "ön tampon";
            case REAR_BUMPER -> "arka tampon";
            case HEADLIGHT -> "ön far";
            case TAIL_LIGHT -> "arka far";
            case HOOD -> "kaput";
            case TRUNK -> "bagaj";
            case GRILLE -> "ön ızgara";
            case FENDER -> "çamurluk";
            case FRONT_WHEEL -> "ön tekerlek";
            case REAR_WHEEL -> "arka tekerlek";
            case WINDSHIELD -> "ön cam";
            case REAR_WINDSHIELD -> "arka cam";
            case MIRROR -> "yan ayna";
            case ROOF -> "tavan";
            case QUARTER_PANEL -> "arka yan panel";
            case ROCKER_PANEL -> "marşpiyel";
            case LICENSE_PLATE -> "plaka";
            default -> vehiclePart
                    .name()
                    .toLowerCase(TURKISH_LOCALE)
                    .replace("_", " ");
        };
    }

    private String formatPrice(
            BigDecimal price
    ) {
        NumberFormat formatter =
                NumberFormat.getNumberInstance(
                        TURKISH_LOCALE
                );

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        return formatter.format(price);
    }

    private String capitalize(
            String value
    ) {
        if (value == null || value.isBlank()) {
            return "";
        }

        return value.substring(0, 1)
                .toUpperCase(TURKISH_LOCALE)
                + value.substring(1);
    }
}