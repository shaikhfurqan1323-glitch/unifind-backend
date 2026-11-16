package com.unifind.backend.repository;

import com.unifind.backend.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByClaimantId(Long userId);
    List<Claim> findByStatus(Claim.ClaimStatus status);
}