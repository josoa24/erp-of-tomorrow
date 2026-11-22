package com.hackaton.auth.user.repository;

import com.hackaton.auth.user.entity.Role;
import com.hackaton.auth.user.entity.UserAccount;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long>, JpaSpecificationExecutor<UserAccount> {

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findByEmailIgnoreCase(String email);

    Optional<UserAccount> findFirstByEmailIgnoreCaseOrderByIdAsc(String email);

    boolean existsByEmailIgnoreCase(String email);

    long countByEnabled(boolean enabled);

    long countByRole(Role role);

    long countByRoleAndEnabled(Role role, boolean enabled);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserAccount u where u.role = :role and u.enabled = true")
    List<UserAccount> lockEnabledByRole(@Param("role") Role role);

    @Modifying
    @Query("update UserAccount u set u.lastLoginAt = :at where u.id = :id")
    int updateLastLoginAt(@Param("id") Long id, @Param("at") Instant at);
}
