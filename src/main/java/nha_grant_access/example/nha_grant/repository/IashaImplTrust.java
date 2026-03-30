package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.AashaImplTrust;
import nha_grant_access.example.nha_grant.entity.ImplementInsuCalc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IashaImplTrust extends JpaRepository<AashaImplTrust, Integer> {
    @Query(value ="Select * from asha_impl_trust where request_id= :requestId and roleId= :roleId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<AashaImplTrust> findByRequestId(String requestId, Integer roleId);
}
