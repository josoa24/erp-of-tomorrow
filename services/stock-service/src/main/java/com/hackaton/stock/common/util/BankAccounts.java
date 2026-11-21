package com.hackaton.stock.common.util;

import java.util.Locale;

/** Coordonnées bancaires : comparaison sans espaces ni tirets, affichage masqué. */
public final class BankAccounts {

    private BankAccounts() {
    }

    public static String clean(String raw) {
        return raw == null || raw.isBlank() ? null : raw.trim();
    }

    public static String normalize(String raw) {
        return raw == null ? null : raw.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
    }

    public static boolean sameAccount(String first, String second) {
        return normalize(first).equals(normalize(second));
    }

    public static String last4(String raw) {
        String normalized = normalize(clean(raw));
        if (normalized == null) {
            return null;
        }
        return normalized.length() <= 4 ? normalized : normalized.substring(normalized.length() - 4);
    }
}
