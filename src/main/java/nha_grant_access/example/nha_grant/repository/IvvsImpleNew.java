package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.ImplementInsuCalc;
import nha_grant_access.example.nha_grant.entity.ImplementTrustCalc;
import nha_grant_access.example.nha_grant.entity.VVSImplementNew;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IvvsImpleNew extends JpaRepository<VVSImplementNew, Integer> {
    @Query(value ="Select * from vvs_implementtrust_calc where request_id= :requestId", nativeQuery = true)
    Optional<VVSImplementNew> findByRequestId(String requestId);
}
