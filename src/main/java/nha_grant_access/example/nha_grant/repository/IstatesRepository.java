package nha_grant_access.example.nha_grant.repository;

import jakarta.transaction.Transactional;
import nha_grant_access.example.nha_grant.entity.States;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface IstatesRepository extends JpaRepository<States, Integer> {
    @Query(value = """
    SELECT 
        SUM(
            CASE 
                WHEN :proposalTypeId = 1 THEN implementation_max_eligible_grant
                WHEN :proposalTypeId = 2 THEN administrative_max_eligible_grant
                WHEN :proposalTypeId = 0 THEN implementation_max_eligible_grant + administrative_max_eligible_grant
                ELSE 0
            END
        ) AS maxEligibleGrant
    FROM states
    WHERE (:includeAllStates = true OR id IN (:stateId))
    """, nativeQuery = true)
    BigDecimal getMaxEligibleGrant(
            Integer proposalTypeId,
          List<Integer> stateId,
            Boolean includeAllStates

    );
    @Modifying
    @Transactional
    @Query(value = "update states set aasha_imp_max_elg_grant= :maxElgGrant WHERE id = :stateId", nativeQuery = true)
    Integer addAshaImp(BigDecimal maxElgGrant, Integer stateId);
    @Modifying
    @Transactional
    @Query(value = "update states set aasha_admin_max_elg_grant= :maxElgGrant WHERE id = :stateId", nativeQuery = true)
    Integer addAshaAdmin(BigDecimal maxElgGrant, Integer stateId);
    @Modifying
    @Transactional
    @Query(value = "update states set vvs_imp_max_elg_grant = :maxElgGrant WHERE id = :stateId", nativeQuery = true)
    Integer addVVSAdmin(BigDecimal maxElgGrant, Integer stateId);
    @Modifying
    @Transactional
    @Query(value = "update states set vvs_admin_max_elg_grant = :maxElgGrant WHERE id = :stateId", nativeQuery = true)
    Integer addVVSImp(BigDecimal maxElgGrant, Integer stateId);

    @Query(value = """
                 SELECT COALESCE(SUM(
                                   implementation_max_eligible_grant +
                                   administrative_max_eligible_grant +
                                   aasha_imp_max_elg_grant +
                                   aasha_admin_max_elg_grant +
                                   vvs_imp_max_elg_grant +
                                   vvs_admin_max_elg_grant
                               ), 0) AS total_grant
                               FROM states
                               WHERE (:stateId = 0 OR id = :stateId)
            """, nativeQuery = true)
    BigDecimal getTotalGrantByState(Integer stateId);

    @Query(value = "SELECT COALESCE(SUM(q1), 0) FROM states WHERE (:stateId = 0 OR id = :stateId)", nativeQuery = true)
    BigDecimal getq1(Integer stateId);

    @Query(value = "SELECT COALESCE(SUM(q2), 0) FROM states WHERE (:stateId = 0 OR id = :stateId)", nativeQuery = true)
    BigDecimal getq2( Integer stateId);

    @Query(value = "SELECT COALESCE(SUM(q3), 0) FROM states WHERE (:stateId = 0 OR id = :stateId)", nativeQuery = true)
    BigDecimal getq3( Integer stateId);

    @Query(value = "SELECT COALESCE(SUM(q4), 0) FROM states WHERE (:stateId = 0 OR id = :stateId)", nativeQuery = true)
    BigDecimal getq4(Integer stateId);








}
