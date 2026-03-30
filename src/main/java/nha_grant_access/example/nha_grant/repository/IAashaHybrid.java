package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.AashaHybrid;
import nha_grant_access.example.nha_grant.entity.AdministrativeCalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IAashaHybrid extends JpaRepository<AashaHybrid, Integer> {

    @Query(value ="Select * from asha_hybrid_calc  where request_id= :requestId  and role_id= :roleId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<AashaHybrid> findByRequestId(String requestId ,Integer roleId);
}

