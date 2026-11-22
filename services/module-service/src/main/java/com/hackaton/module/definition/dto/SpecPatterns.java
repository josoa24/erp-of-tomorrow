package com.hackaton.module.definition.dto;

final class SpecPatterns {

    static final String KEY = "^[a-z][a-z0-9_]{0,39}$";
    static final String KEY_MESSAGE = "doit être en minuscules (lettres, chiffres, _), 40 caractères au plus";
    static final String MODULE_ICON = "^(truck|wrench|calendar-days|package|clipboard-list|layout-grid|users|receipt)$";
    static final String MODULE_ICON_MESSAGE =
            "doit être truck, wrench, calendar-days, package, clipboard-list, layout-grid, users ou receipt";
    static final String ICON = "^[a-z0-9-]{1,40}$";
    static final String ICON_MESSAGE = "doit être un nom d'icône en minuscules";

    private SpecPatterns() {
    }
}
