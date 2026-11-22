package com.hackaton.module.adaptation.catalog;

import com.hackaton.module.common.domain.BuiltInModules;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class AdaptationCatalog {

    public static final String STOCK_EXPIRY_LOTS = "stock.lots-peremption";
    public static final String SALES_MOBILE_MONEY_CREDIT = "ventes.mobile-money-credit";
    public static final String RELATIONS_TAX_ID_CREDIT_LIMIT = "relations.nif-plafond-credit";
    public static final String TEAM_SALARY_ADVANCES = "equipe.avances-salaire";

    private static final Map<String, String> MODULE_BY_ADAPTATION = Map.of(
            STOCK_EXPIRY_LOTS, BuiltInModules.STOCK,
            SALES_MOBILE_MONEY_CREDIT, BuiltInModules.VENTES,
            RELATIONS_TAX_ID_CREDIT_LIMIT, BuiltInModules.RELATIONS,
            TEAM_SALARY_ADVANCES, BuiltInModules.EQUIPE);

    public Optional<String> moduleOf(String adaptationKey) {
        return Optional.ofNullable(adaptationKey).map(MODULE_BY_ADAPTATION::get);
    }
}
