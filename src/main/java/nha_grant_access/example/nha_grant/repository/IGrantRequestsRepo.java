package nha_grant_access.example.nha_grant.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IGrantRequestsRepo extends JpaRepository<GrantRequests, Integer> {
    @Query(value = """
    SELECT * FROM grant_requests
    WHERE state_id = :stateId
    AND (:userId IS NULL OR user_id = :userId)
    """, nativeQuery = true)
    List<GrantRequests> findAllGrantRequest(Integer stateId, Integer userId);
    @Query(value = "SELECT gr.request_id, gr.remarks, gr.requested_amount, u.name , gr.created_at  " +
            "FROM grant_requests gr " +
            "JOIN users u ON gr.user_id = u.id " +
            "WHERE gr.state_id = :stateId AND gr.flow_status = :flowStatus",
            nativeQuery = true)
    List<Object[]> findAllGrantRequestByStatus(Integer stateId , Integer flowStatus);

    @Query(value ="Select * from grant_requests where request_id= :requestId", nativeQuery = true)
    Optional<GrantRequests> findByRequestId(String requestId);
    @Query(value ="Select * from grant_requests where request_id= :requestId", nativeQuery = true)
GrantRequests findGrantRequestByRequestId(String requestId);
    @Query(value = "SELECT q.id, gr.request_id, q.query_comment, q.query_doc, q.created_at, u.name " +
            "FROM grant_requests gr " +
            "JOIN queries q ON q.request_id = gr.request_id " +
            "JOIN users u ON q.query_user_id = u.id " +
            "WHERE gr.user_id = :userId AND gr.flow_status = 1", nativeQuery = true)
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




    @Modifying
    @Transactional
    @Query(value = "UPDATE grant_requests SET flow_status = :statusId WHERE request_id = :requestId", nativeQuery = true)
    int updateStatusDescription( String requestId,  Integer statusId);

    @Query(value = """
    SELECT 
        COALESCE(SUM(gr.requested_amount), 0) AS total_requested_amount,
        COALESCE(SUM(gr.released_amount), 0) AS total_released_amount,
        COUNT(*) FILTER (WHERE gr.flow_status = 4) AS nha_review,
        COUNT(*) FILTER (WHERE gr.flow_status = 2) AS pending_proposals,
        COUNT(*) FILTER (WHERE gr.flow_status = 7) AS pending_query,
        COUNT(*) FILTER (WHERE gr.flow_status = 1) AS sha_pending,
        (
            SELECT COUNT(*) 
            FROM work_flow wf 
            WHERE wf.user_id = :userId AND wf.action_id = 7
        ) AS resolved_query
    FROM grant_requests gr
    WHERE gr.state_id = :stateId
""", nativeQuery = true)
    List<Object[]>getStateCeoDashboard(Integer stateId,  Integer userId);

    @Query(value = """
    SELECT DISTINCT policy_start_date AS policyStartDate, policy_end_date AS policyEndDate
    FROM grant_requests 
    WHERE state_id = :stateId
""", nativeQuery = true)
    List<Object[]> findDistinctPolicyPeriodsByStateId(Integer stateId);

}
