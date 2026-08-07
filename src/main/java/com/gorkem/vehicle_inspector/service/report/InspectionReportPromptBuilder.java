package com.gorkem.vehicle_inspector.service.report;

import com.gorkem.vehicle_inspector.dto.llm.DamageContext;
import com.gorkem.vehicle_inspector.dto.llm.InspectionLlmRequest;

import java.util.stream.Collectors;

public final class InspectionReportPromptBuilder {

    private InspectionReportPromptBuilder() {
    }

    public static String build(
            InspectionLlmRequest request
    ) {
        String damageText =
                request.damages()
                        .stream()
                        .map(
                                InspectionReportPromptBuilder
                                        ::formatDamage
                        )
                        .collect(
                                Collectors.joining("\n")
                        );

        return """
                Bir araç hasar inceleme uygulaması için
                kullanıcıya gösterilecek Türkçe raporu oluştur.

                ML tarafından sağlanan hasar bilgilerini
                gerçek kabul et. Yeni hasar türleri uydurma.

                Araç:
                Marka: %s
                Model: %s
                Model yılı: %d
                Kilometre: %d

                İnceleme şehri: %s
                İnceleme tarihi: %s

                Genel hasar seviyesi: %s
                Güven skoru: %s

                ML analiz mesajı:
                %s

                Hasarlar:
                %s

                Görevlerin:

                1. ML sonuçlarını kullanıcı dostu Türkçe ile açıkla.
                2. Önerilen onarım işlemlerini açıkla.
                3. Web aramasını kullanarak %s şehrindeki güncel
                   Türkiye otomotiv servis ve onarım fiyatlarını araştır.
                4. Araç marka, model, model yılı, hasarlı parça,
                   hasar türü ve önerilen işlemleri dikkate al.
                5. Makul bir minimum ve maksimum fiyat aralığı üret.
                6. İnternette yeterli güvenilir fiyat bilgisi yoksa
                   kesin fiyat uydurma; geniş ve temkinli bir aralık kullan.
                7. Fiyatın servis, kullanılan parça, işçilik ve hasarın
                   gerçek durumuna göre değişebileceğini belirt.
                8. Para birimi TRY olsun.
                9. Nihai rapor Türkçe olsun.
                """
                .formatted(
                        request.vehicle().brand(),
                        request.vehicle().model(),
                        request.vehicle().modelYear(),
                        request.vehicle().mileage(),
                        request.city(),
                        request.analysisDate(),
                        request.damageSeverity(),
                        request.confidenceScore(),
                        request.analysisMessage(),
                        damageText,
                        request.city()
                );
    }

    private static String formatDamage(
            DamageContext damage
    ) {
        return """
                - Hasar türü: %s
                  Önerilen işlem: %s
                  Parça değişimi gerekli: %s
                  Etkilenen parçalar: %s
                """
                .formatted(
                        damage.damageType(),
                        damage.recommendedAction(),
                        damage.partReplacementRequired(),
                        damage.affectedParts()
                );
    }
}