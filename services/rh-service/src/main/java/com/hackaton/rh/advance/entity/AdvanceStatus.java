package com.hackaton.rh.advance.entity;

import java.util.List;

public enum AdvanceStatus {
    DRAFT,
    VALIDATED,
    REJECTED;

    public static final List<AdvanceStatus> COMMITTED = List.of(DRAFT, VALIDATED);
}
