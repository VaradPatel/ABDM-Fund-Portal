package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.States;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface IstatesRepository extends JpaRepository<States, Integer> {

    @Query(value = """
    SELECT 
        CASE 
            WHEN :proposalTypeId = 1 THEN implementation_max_eligible_grant
            WHEN :proposalTypeId = 2 THEN administrative_max_eligible_grant
            WHEN :proposalTypeId = 0 THEN implementation_max_eligible_grant + administrative_max_eligible_grant
        END AS maxEligibleGrant
    FROM states
    WHERE (:includeAllStates = true OR id IN (:stateId))
    """, nativeQuery = true)
    BigDecimal getMaxEligibleGrant(
            Integer proposalTypeId,
          List<Integer> stateId,
            Boolean includeAllStates

    );

}
