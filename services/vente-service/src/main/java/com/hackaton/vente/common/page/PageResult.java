package com.hackaton.vente.common.page;

import java.util.List;

public record PageResult<T>(List<T> items, long total) {

    private static final int MAX_SIZE = 100;

    public static <T> PageResult<T> of(List<T> all, Integer page, Integer size) {
        if (size == null) {
            return new PageResult<>(all, all.size());
        }
        int safeSize = Math.max(1, Math.min(size, MAX_SIZE));
        int safePage = page == null ? 0 : Math.max(0, page);
        int from = (int) Math.min((long) safePage * safeSize, all.size());
        int to = Math.min(from + safeSize, all.size());
        return new PageResult<>(all.subList(from, to), all.size());
    }
}
