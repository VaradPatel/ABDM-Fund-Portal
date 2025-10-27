package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.AdministrativeCalc;
import nha_grant_access.example.nha_grant.entity.ImplementTrustCalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImplementTrustCalcRepo extends JpaRepository<ImplementTrustCalc, Integer> {
    @Query(value ="Select * from implement_trust_calc where request_id= :requestId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<ImplementTrustCalc> findByRequestId(String requestId);
}
