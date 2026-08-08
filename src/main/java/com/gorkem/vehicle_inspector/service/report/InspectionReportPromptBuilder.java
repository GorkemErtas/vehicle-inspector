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
                Vehicle Inspector uygulaması için
                kullanıcıya gösterilecek Türkçe bir
                araç hasar raporu oluştur.

                Hasarın kendisi hakkında yalnızca
                ML sisteminin sağladığı bilgileri kullan.

                ML tarafından belirtilmeyen yeni bir
                hasar türü, araç parçası veya hasar
                seviyesi uydurma.

                ARAÇ BİLGİLERİ

                Marka: %s
                Model: %s
                Model yılı: %d
                Kilometre: %d

                KONUM

                Şehir: %s
                Analiz tarihi: %s

                ML ANALİZİ

                Hasar seviyesi: %s
                Güven skoru: %s

                ML analiz mesajı:
                %s

                HASARLAR

                %s

                GÖREV

                1. ML sonuçlarını kullanıcı dostu ve
                   anlaşılır Türkçe ile açıkla.

                2. ML tarafından önerilen onarım
                   işlemlerini açıkla.

                3. Google Search kullanarak özellikle
                   %s şehrindeki güncel Türkiye
                   otomotiv servis, kaporta, boya,
                   parça ve işçilik fiyatlarını araştır.

                4. Fiyat tahmininde şunları dikkate al:

                   - araç markası
                   - araç modeli
                   - model yılı
                   - kilometre
                   - hasarlı parçalar
                   - hasar türleri
                   - hasar seviyesi
                   - önerilen onarım işlemleri
                   - parça değişimi gerekip gerekmediği
                   - seçilen şehir
                   - güncel işçilik ve parça fiyatları

                5. estimatedMinimumPrice ve
                   estimatedMaximumPrice alanları,
                   rapordaki TÜM hasarların tahmini
                   TOPLAM onarım maliyetini temsil etsin.

                6. Fiyatları %s şehrindeki güncel
                   piyasa koşullarına göre belirle.

                7. Yeterli doğrudan fiyat verisi
                   bulunamazsa benzer servis,
                   kaporta, boya veya parça fiyatlarını
                   kullanarak temkinli bir aralık oluştur.

                8. Güvenilir veri olmadan aşırı kesin
                   bir rakam üretme.

                9. Minimum fiyat maksimum fiyattan
                   büyük olamaz.

                10. Para birimi her zaman TRY olsun.

                11. priceInformation alanında fiyatın
                    neden bu aralıkta olduğunu açıkla.

                12. priceSourceDescription alanında
                    fiyat tahmini için kullanılan
                    güncel kaynak türlerini kısaca açıkla.

                13. Bunun kesin servis teklifi değil,
                    yaklaşık piyasa tahmini olduğunu
                    disclaimer alanında açıkça belirt.

                14. Nihai rapor tamamen Türkçe olsun.

                JSON dışında ek açıklama üretme.
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
                        request.city(),
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