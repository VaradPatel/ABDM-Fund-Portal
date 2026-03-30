package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.AdministrativeCalc;
import nha_grant_access.example.nha_grant.entity.Dashboard;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface IAdminCalcRepo extends JpaRepository<AdministrativeCalc, Integer> {
    @Query(value ="Select * from administrative_calc where request_id= :requestId and roleId= :roleId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<AdministrativeCalc> findByRequestId(String requestId , Integer roleId);
}
