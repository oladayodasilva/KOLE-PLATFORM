package com.kole.platform.identity.domain.repository;

import com.kole.platform.identity.domain.model.RefreshToken;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RefreshTokenRepository
    extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findAllByFamilyId(UUID familyId);

    @Modifying
    @Query("""
        delete from RefreshToken token
        where token.expiresAt < :cutoff
        """)
    int deleteExpiredTokens(Instant cutoff);
}