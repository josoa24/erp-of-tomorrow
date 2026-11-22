package com.hackaton.stock.common.util;

import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class SearchText {

    private static final Pattern ACCENTS = Pattern.compile("\\p{M}+");

    private SearchText() {
    }

    public static String normalize(String text) {
        if (text == null) {
            return "";
        }
        String decomposed = Normalizer.normalize(text.trim(), Normalizer.Form.NFD);
        return ACCENTS.matcher(decomposed).replaceAll("").toLowerCase(Locale.ROOT);
    }

    public static boolean anyContains(String search, String... values) {
        String needle = normalize(search);
        return needle.isEmpty() || Stream.of(values).anyMatch(value -> normalize(value).contains(needle));
    }
}
