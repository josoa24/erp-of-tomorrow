package com.hackaton.gateway.security;

import java.util.List;

final class RolePriority {

    private static final List<String> ORDER = List.of("ADMIN", "STOCK", "VENTE", "RH", "CRM");

    private RolePriority() {
    }

    static int rank(String role) {
        int index = ORDER.indexOf(role);
        return index < 0 ? ORDER.size() : index;
    }
}
