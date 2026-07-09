package abdm_nha_grant_access.example.nha_grant.repository;

import abdm_nha_grant_access.example.nha_grant.entity.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface IProposalRepo extends JpaRepository<Proposal, Integer> {

    Optional<Proposal> findByRequestId(String requestId);

    @Query("SELECT p FROM Proposal p WHERE " +
            "(:stateId IS NULL OR p.stateId = :stateId) AND " +
            "(:financialYear IS NULL OR p.financialYear = :financialYear) AND " +
            "(:quarter IS NULL OR p.quarter = :quarter) AND " +
            "(:categoryId IS NULL OR p.categoryId = :categoryId) " +
            "ORDER BY p.createdAt DESC")
    List<Proposal> search(@Param("stateId") Integer stateId,
                           @Param("financialYear") String financialYear,
                           @Param("quarter") String quarter,
                           @Param("categoryId") Integer categoryId);
}
