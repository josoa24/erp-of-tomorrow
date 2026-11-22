package com.hackaton.auth.user.repository;

import com.hackaton.auth.user.dto.UserSearchCriteria;
import com.hackaton.auth.user.entity.UserAccount;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.data.jpa.domain.Specification;

public final class UserSpecifications {

    private static final char ESCAPE = '\\';

    private UserSpecifications() {
    }

    public static Specification<UserAccount> matching(UserSearchCriteria criteria) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (criteria.search() != null && !criteria.search().isBlank()) {
                String pattern = "%" + escape(criteria.search().trim().toLowerCase(Locale.ROOT)) + "%";
                predicates.add(builder.or(
                        builder.like(builder.lower(builder.coalesce(root.<String>get("fullName"), "")), pattern, ESCAPE),
                        builder.like(builder.lower(root.get("email")), pattern, ESCAPE)));
            }
            if (criteria.roles() != null && !criteria.roles().isEmpty()) {
                query.distinct(true);
                predicates.add(root.join("roles", JoinType.INNER).in(criteria.roles()));
            }
            if (criteria.enabled() != null) {
                predicates.add(builder.equal(root.get("enabled"), criteria.enabled()));
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
