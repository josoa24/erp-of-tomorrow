package com.hackaton.vente.sale.service.reader;

import java.text.Normalizer;
import java.util.Locale;

public final class SearchText {

    private SearchText() {
    }

    public static String normalize(String value) {
        if (value == null) {
            return "";
        }
        String withoutAccents = Normalizer.normalize(value, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return withoutAccents.toLowerCase(Locale.ROOT).trim();
    }

    public static boolean contains(String value, String normalizedTerm) {
        return normalizedTerm.isEmpty() || normalize(value).contains(normalizedTerm);
    }
}
