package com.hackaton.auth.user.entity;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class RolePriority {

    private RolePriority() {
    }

    public static List<Role> sorted(Collection<Role> roles) {
        return roles.stream().distinct().sorted(Comparator.comparingInt(Role::ordinal)).toList();
    }

    public static Role primary(Collection<Role> roles) {
        return sorted(roles).get(0);
    }

    public static Set<Role> merge(List<Role> roles, Role legacyRole) {
        List<Role> source = roles != null && !roles.isEmpty() ? roles : legacyRole == null ? List.of() : List.of(legacyRole);
        return source.stream().filter(java.util.Objects::nonNull).collect(Collectors.toSet());
    }
}
