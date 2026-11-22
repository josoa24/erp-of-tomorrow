package com.hackaton.auth.user.repository;

import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.UserAccount;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>, JpaSpecificationExecutor<UserAccount> {

    Optional<UserAccount> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByEnabled(boolean enabled);

    long countByRole(Role role);

    @Modifying
    @Query("update UserAccount u set u.lastLoginAt = :at where u.id = :id")
    int updateLastLoginAt(@Param("id") Long id, @Param("at") Instant at);
}
