package nha_grant_access.example.nha_grant.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import nha_grant_access.example.nha_grant.dto.AllGrantRequest;
import nha_grant_access.example.nha_grant.dto.GetActiveQuery;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.dto.Test;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IGrantRequestsRepo extends JpaRepository<GrantRequests, Integer> {
    @Query(value ="Select * from grant_requests where user_id=:userId", nativeQuery = true)
    List<GrantRequests> findAllGrantRequest(Integer userId);
    @Query(value ="Select * from grant_requests where request_id=:requestId", nativeQuery = true)
GrantRequests findGrantRequestByRequestId(String requestId);
    @Query("SELECT gr.requestId, q.queryComment, q.queryDoc " +
            "FROM GrantRequests gr " +
            "JOIN Queries q ON q.requestId = gr.requestId " +
            "WHERE gr.user.id = :userId AND gr.statusDescription.id = 1")
    List<Object[]> findActiveQueryFromUserID(Integer userId);

    boolean existsByRequestId(String requestId);



    @Query(value = """
        SELECT 
            COALESCE(SUM(requested_amount), 0) AS totalRequestedAmount,
            COALESCE(SUM(released_amount), 0) AS totalReleasedAmount,
            COUNT(*) FILTER (WHERE flow_status = 9) AS completedProposals,
            COUNT(*) FILTER (WHERE flow_status != 9) AS pendingProposals,
            COUNT(*) FILTER (WHERE flow_status = 1) AS pendingQuery,
            (SELECT COUNT(*) FROM work_flow WHERE user_id = :userId AND action_id = 7) AS respondedQuery
        FROM grant_requests
        WHERE user_id = :userId
    """, nativeQuery = true)
    List<Object[]> shaFinanceDashboardDetails(Integer userId);



}
