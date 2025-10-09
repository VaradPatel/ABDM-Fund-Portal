package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.UserStateRole;
import nha_grant_access.example.nha_grant.entity.VVSImplementNew;
import nha_grant_access.example.nha_grant.entity.VvsHybrid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IVvsHybrid  extends JpaRepository<VvsHybrid, Integer> {
    @Query(value ="Select * from vvs_hybrid_calc where request_id= :requestId", nativeQuery = true)
    Optional<VvsHybrid> findByRequestId(String requestId);
}
