package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.VVSImplementNew;
import nha_grant_access.example.nha_grant.entity.VvsAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface VvsAdminRepo extends JpaRepository<VvsAdmin, Integer> {
    @Query(value ="Select * from vvs_admin_calc where request_id= :requestId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<VvsAdmin> findByRequestId(String requestId);
}

