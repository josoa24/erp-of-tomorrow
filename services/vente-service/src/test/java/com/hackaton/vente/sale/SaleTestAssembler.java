package com.hackaton.vente.sale;

import com.hackaton.vente.sale.entity.Sale;
import com.hackaton.vente.sale.mapper.SaleMapper;
import com.hackaton.vente.sale.mapper.SaleResponseAssembler;
import com.hackaton.vente.sale.service.facts.SaleFacts;
import com.hackaton.vente.sale.service.facts.SaleFactsLoader;
import com.hackaton.vente.sale.service.status.PaymentStatusResolver;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

public final class SaleTestAssembler {

    public static final ZoneId ZONE = ZoneId.of("Indian/Antananarivo");
    public static final String NOW = "2026-11-24T07:00:00Z";

    private SaleTestAssembler() {
    }

    public static Clock clockAt(String instant) {
        return Clock.fixed(Instant.parse(instant), ZONE);
    }

    public static SaleResponseAssembler withoutFacts() {
        return withoutFacts(clockAt(NOW));
    }

    public static SaleResponseAssembler withoutFacts(Clock clock) {
        SaleFactsLoader emptyLoader = new SaleFactsLoader(null, null, null) {
            @Override
            public SaleFacts load(List<Sale> sales) {
                return SaleFacts.none();
            }
        };
        return new SaleResponseAssembler(emptyLoader, new SaleMapper(new PaymentStatusResolver(clock)));
    }
}
