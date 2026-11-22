package com.hackaton.crm.contact.mapper;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Initials {

    private static final String UNKNOWN = "?";

    private Initials() {
    }

    public static String of(String name) {
        if (name == null || name.isBlank()) {
            return UNKNOWN;
        }
        List<String> words = Arrays.stream(name.trim().split("\\s+")).toList();
        String initials = words.size() == 1
                ? firstLetters(words.get(0), 2)
                : firstLetters(words.get(0), 1) + firstLetters(words.get(1), 1);
        return initials.toUpperCase(Locale.FRENCH);
    }

    private static String firstLetters(String word, int count) {
        int end = word.offsetByCodePoints(0, Math.min(count, word.codePointCount(0, word.length())));
        return word.substring(0, end);
    }
}
