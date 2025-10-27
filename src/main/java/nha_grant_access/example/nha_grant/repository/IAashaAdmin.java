package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.AashaAdmin;
import nha_grant_access.example.nha_grant.entity.AashaHybrid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IAashaAdmin extends JpaRepository<AashaAdmin, Integer> {
    @Query(value ="Select * from aasha_admin_calc where request_id= :requestId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<AashaAdmin> findByRequestId(String requestId);
}

