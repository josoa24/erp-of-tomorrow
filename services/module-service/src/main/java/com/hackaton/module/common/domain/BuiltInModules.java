package com.hackaton.module.common.domain;

import java.util.Set;

public final class BuiltInModules {

    public static final String STOCK = "stock";
    public static final String VENTES = "ventes";
    public static final String RELATIONS = "relations";
    public static final String EQUIPE = "equipe";
    public static final Set<String> KEYS = Set.of(STOCK, VENTES, RELATIONS, EQUIPE);

    private BuiltInModules() {
    }
}
