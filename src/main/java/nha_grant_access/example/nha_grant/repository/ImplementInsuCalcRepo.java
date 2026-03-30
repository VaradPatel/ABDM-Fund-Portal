package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.ImplementInsuCalc;
import nha_grant_access.example.nha_grant.entity.ImplementTrustCalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ImplementInsuCalcRepo extends JpaRepository<ImplementInsuCalc, Integer> {
    @Query(value ="Select * from implement_insurance_hybrid_calc where request_id= :requestId  and roleId= :roleId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<ImplementInsuCalc> findByRequestId(String requestId , Integer roleId);
}
