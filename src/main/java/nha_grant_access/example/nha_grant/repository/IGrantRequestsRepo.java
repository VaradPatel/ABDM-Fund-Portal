package nha_grant_access.example.nha_grant.repository;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IGrantRequestsRepo extends JpaRepository<GrantRequests, Integer> {
    @Query(value = """
    SELECT * FROM grant_requests
    WHERE state_id IN ( :stateId)
    AND (:userId IS NULL OR user_id = :userId) order by grant_requests.created_at desc
    """, nativeQuery = true)
    List<GrantRequests> findAllGrantRequest(List<Integer>stateId, Integer userId);


    @Query(value = "SELECT gr.request_id, gr.remarks, gr.requested_amount, u.name AS user_name, gr.created_at, s.name AS state_name " +
            "FROM grant_requests gr " +
            "JOIN users u ON gr.user_id = u.id " +
            "JOIN states s ON gr.state_id = s.id " +
            "WHERE gr.state_id = :stateId AND gr.flow_status = :flowStatus  order by gr.created_at desc ",
            nativeQuery = true)
    List<Object[]> findAllGrantRequestByStatus(Integer stateId , Integer flowStatus);


    @Query(value = "SELECT DISTINCT ON (gr.request_id) " +
            "gr.request_id, " +

            "gr.remarks, " +
            "gr.requested_amount, " +
            "u.name AS user_name, " +
            "gr.created_at, " +
            "s.name AS state_name " +
            "FROM " +
            "grant_requests gr " +
            "JOIN work_flow wf ON wf.request_id = gr.request_id " +
            "JOIN user_state_role usr ON wf.user_id = usr.user_id AND usr.role_id = :roleId " +
            "JOIN users u ON usr.user_id = u.id " +
            "JOIN states s ON gr.state_id = s.id " +
            "WHERE gr.state_id IN (:stateId) " +
            "AND gr.flow_status = :flowStatus",
            nativeQuery = true)
    List<Object[]> getGrantRequestsWithState( List<Integer>stateId, Integer flowStatus, Integer roleId);


    @Query(value ="Select * from grant_requests where request_id= :requestId", nativeQuery = true)
    Optional<GrantRequests> findByRequestId(String requestId);
    @Query(value ="Select * from grant_requests where request_id= :requestId", nativeQuery = true)
GrantRequests findGrantRequestByRequestId(String requestId);
    @Query(value = "SELECT q.id, gr.request_id, q.query_comment, q.query_doc, q.created_at, u.name " +
            "FROM grant_requests gr " +
            "JOIN queries q ON q.request_id = gr.request_id " +
            "JOIN users u ON q.query_user_id = u.id " +
            "WHERE gr.state_id = :stateId AND gr.flow_status = 1 and q.active = true and u.role_id = 2 ", nativeQuery = true)
    List<Object[]> findActiveQueryFromUserID(Integer stateId);
    @Query(value = "SELECT q.id, gr.request_id, q.query_comment, q.query_doc, q.created_at, u.name, s.id AS state_id, s.name AS state_name , gr.proposal_type_id " +
            "FROM grant_requests gr " +
            "JOIN queries q ON q.request_id = gr.request_id " +
            "JOIN users u ON q.query_user_id = u.id " +
            "JOIN states s ON s.id = gr.state_id " +
            "WHERE gr.state_id = :stateId AND gr.flow_status = 10 AND q.active = true and u.role_id = 4 ",
            nativeQuery = true)
    List<Object[]> findStateActiveQueryFromStateID(Integer stateId);

    @Query(value = "SELECT q.id, gr.request_id, q.query_comment, q.query_doc, q.created_at, u.name, s.name AS state_name, gr.proposal_type_id , s.id as state_id " +
            "FROM grant_requests gr " +
            "JOIN queries q ON q.request_id = gr.request_id " +
            "JOIN users u ON q.query_user_id = u.id " +
            "JOIN states s ON gr.state_id = s.id " +
            "WHERE gr.state_id IN (:stateId) AND gr.flow_status = 7 AND q.active = true and u.role_id = 4 ",
            nativeQuery = true)
    List<Object[]> findStateCordActiveQuery(List<Integer>stateId);
    boolean existsByRequestId(String requestId);

    @Query(value = "SELECT q.id, gr.request_id, q.query_comment, q.query_doc, q.created_at, u.name, s.name, gr.proposal_type_id , s.id as state_id " +
            "FROM grant_requests gr " +
            "JOIN queries q ON q.request_id = gr.request_id " +
            "JOIN users u ON q.query_user_id = u.id " +
            "JOIN states s ON gr.state_id = s.id " +
            "WHERE (:stateId = 0 OR gr.state_id = :stateId) " +
            "AND gr.flow_status = 5 " +
            "And u.role_id = 3 " +
            "AND q.active = true",
            nativeQuery = true) List<Object[]> findNhaReviewerActiveQuery(Integer stateId);



    @Query(value = """
    SELECT 
        COALESCE(SUM(requested_amount), 0) AS totalRequestedAmount,
        COALESCE(SUM(released_amount), 0) AS totalReleasedAmount,
        COUNT(*) FILTER (WHERE flow_status = 9) AS completedProposals,
        COUNT(*) FILTER (WHERE flow_status != 9) AS pendingProposals,
        COUNT(*) FILTER (WHERE flow_status = 1) AS pendingQuery,
        (SELECT COUNT(*) FROM work_flow WHERE user_id = :userId AND action_id = 7) AS respondedQuery
       
    FROM grant_requests gr
    WHERE user_id = :userId
      AND (:proposalType = 0 OR proposal_type_id = :proposalType)
      AND (:policyStartDate = 'ALL' OR TO_CHAR(gr.policy_start_date, 'YYYY-MM-DD') = :policyStartDate)
      AND (:policyEndDate = 'ALL' OR TO_CHAR(gr.policy_end_date, 'YYYY-MM-DD') = :policyEndDate)
""", nativeQuery = true)


    List<Object[]> shaFinanceDashboardDetails(Integer userId, String policyStartDate , String policyEndDate, Integer proposalType, Integer stateId);
@Query(value="select count(*) from grant_requests gr where state_id= :stateId and flow_status=1",nativeQuery = true)
Integer totalShaPendingQueriesByState(Integer stateId);


    @Modifying
    @Transactional
    @Query(value = "UPDATE grant_requests SET flow_status = :statusId WHERE request_id = :requestId", nativeQuery = true)
    int updateStatusDescription( String requestId,  Integer statusId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE grant_requests SET e_sign_status_state_ceo = :status WHERE request_id = :requestId", nativeQuery = true)
    int updateESignStatus( String requestId,  boolean status);

    @Query(value = """
                SELECT 
                    COALESCE(SUM(gr.requested_amount), 0) AS total_requested_amount,
                    COALESCE(SUM(gr.released_amount), 0) AS total_released_amount,
                    COUNT(*) FILTER (WHERE gr.flow_status = 4) AS nha_review,
                    COUNT(*) FILTER (WHERE gr.flow_status = 2) AS pending_proposals,
                    COUNT(*) FILTER (WHERE gr.flow_status = 10) AS pending_query,
                    COUNT(*) FILTER (WHERE gr.flow_status = 1) AS sha_pending,
                    (
                        SELECT COUNT(*) 
                        FROM work_flow wf 
                        WHERE wf.user_id = :userId AND wf.action_id = 7
                    ) AS resolved_query
                FROM grant_requests gr
                WHERE gr.state_id = :stateId  
                 AND (:proposalType = 0 OR proposal_type_id = :proposalType)
                AND (:policyStartDate = 'ALL' OR TO_CHAR(gr.policy_start_date, 'YYYY-MM-DD') = :policyStartDate)
                                 AND (:policyEndDate = 'ALL' OR TO_CHAR(gr.policy_end_date, 'YYYY-MM-DD') = :policyEndDate)
                
            """, nativeQuery = true)
    List<Object[]>getStateCeoDashboard(Integer stateId,  Integer userId, String policyStartDate, String policyEndDate, Integer proposalType);

    @Query(value = """
                SELECT 
                    COALESCE(SUM(gr.requested_amount), 0) AS total_requested_amount,
                    COALESCE(SUM(gr.released_amount), 0) AS total_released_amount,
                    COUNT(*) FILTER (WHERE gr.flow_status = 4) AS pending,
                    COUNT(*) FILTER (WHERE gr.flow_status = 8) AS pending_for_sanction,
                    COUNT(*) FILTER (WHERE gr.flow_status = 6) AS pending_at_nhareviewer,
                    COUNT(*) FILTER (WHERE gr.flow_status = 9) AS sanction_upload,
                    COUNT(*) FILTER (WHERE gr.flow_status = 7) AS pending_query,
                    COUNT(*) FILTER (WHERE gr.flow_status = 5) AS query_raised,
                    (
                        SELECT COUNT(*) 
                        FROM work_flow wf 
                        WHERE wf.user_id = :userId AND wf.action_id = 7
                    ) AS resolved_query 
                 
                FROM grant_requests gr
                WHERE gr.state_id IN (:stateId) 
                 AND (:proposalType = 0 OR proposal_type_id = :proposalType)
               AND (:policyStartDate = 'ALL' OR TO_CHAR(gr.policy_start_date, 'YYYY-MM-DD') = :policyStartDate)
                                AND (:policyEndDate = 'ALL' OR TO_CHAR(gr.policy_end_date, 'YYYY-MM-DD') = :policyEndDate)
                
            """, nativeQuery = true)

    List<Object[]>getStateCordDashboard(Integer userId,  List<Integer>stateId , String policyStartDate , String policyEndDate, Integer proposalType);

    @Query(value = """
                SELECT 
                    COALESCE(SUM(gr.requested_amount), 0) AS total_requested_amount,
                    COALESCE(SUM(gr.released_amount), 0) AS total_released_amount,
                    COUNT(*) FILTER (WHERE gr.flow_status = 6) AS pending,
                    COUNT(*) FILTER (WHERE gr.flow_status = 8) AS accepted,
                    COUNT(*) FILTER (WHERE gr.flow_status = 5) AS pending_query,
                    COUNT(*) FILTER (WHERE gr.flow_status = 7) AS query_raised,
                    (
                        SELECT COUNT(*) 
                        FROM work_flow wf 
                        WHERE wf.user_id = :userId AND wf.action_id = 7
                    ) AS resolved_query  
                FROM grant_requests gr
                WHERE (:stateId = 0 OR gr.state_id = :stateId ) 
                 AND (:proposalType = 0 OR proposal_type_id = :proposalType)
                 AND (:policyStartDate = 'ALL' OR TO_CHAR(gr.policy_start_date, 'YYYY-MM-DD') = :policyStartDate)
                      AND (:policyEndDate = 'ALL' OR TO_CHAR(gr.policy_end_date, 'YYYY-MM-DD') = :policyEndDate)
                                                                    
            """, nativeQuery = true)
    List<Object[]>getNhaReviewerDashboard(Integer userId,  Integer stateId, String policyStartDate, String policyEndDate,Integer proposalType);

    @Query(value = """
    SELECT 
        COALESCE(SUM(gr.requested_amount), 0) AS total_requested_amount,
        COALESCE(SUM(gr.released_amount), 0) AS total_released_amount,
        COUNT(*) FILTER (WHERE gr.flow_status = 9) AS approved,
        COUNT(*) FILTER (WHERE gr.flow_status = 8) AS accepted,
        COUNT(*) FILTER (WHERE gr.flow_status NOT IN (8, 9)) AS review,

        (
            SELECT COUNT(*)
            FROM users u
            WHERE u.is_activated = false
        ) AS deactivated_users,

        (
            SELECT COUNT(*)
            FROM users u
            WHERE u.is_verified = true AND u.role_id > 1
        ) AS verified_users,

        (
            SELECT COUNT(*)
            FROM users u
            WHERE u.is_verified = false AND u.role_id > 1
        ) AS pending_users,

        COALESCE(SUM(gr.gc_amount), 0) AS total_released_amount_gc,
        COALESCE(SUM(gr.sc_amount), 0) AS total_released_amount_sc,
        COALESCE(SUM(gr.st_amount), 0) AS total_released_amount_st

    FROM grant_requests gr
    WHERE (:stateId = 0 OR gr.state_id = :stateId)
     AND (:proposalType = 0 OR proposal_type_id = :proposalType)
      AND (:financialYear = 'ALL' OR financial_year = :financialYear)
      
""", nativeQuery = true)


    List<Object[]>getNhaAdminDashboard(Integer stateId, String financialYear, Integer proposalType);




    @Query(value = """
    SELECT 
        policy_start_date AS policyStartDate,
        policy_end_date AS policyEndDate,
        SUM(released_amount) AS totalAmountReleaseTillDate
    FROM grant_requests
    WHERE state_id IN (:stateIds)
      AND (:proposalType = 0 OR proposal_type_id = :proposalType)
    GROUP BY policy_start_date, policy_end_date
    """, nativeQuery = true)
    List<Object[]> findDistinctPolicyPeriodsByStateId(List<Integer> stateIds, Integer proposalType);

    @Query(value = """
    SELECT 
        policy_start_date AS policyStartDate,
        policy_end_date AS policyEndDate,
        SUM( released_amount ) AS totalAmountReleaseTillDate
    FROM grant_requests
    GROUP BY policy_start_date, policy_end_date
    """, nativeQuery = true)
    List<Object[]> findAllDistinctPolicyPeriods();


    @Query(value = "SELECT gr.request_id, gr.remarks, gr.requested_amount, u.name AS user_name, gr.created_at, s.name AS state_name " +
            "FROM grant_requests gr " +
            "JOIN user_state_role usr ON gr.state_id = usr.state_id AND usr.role_id = 3 " +
            "JOIN users u ON usr.user_id = u.id " +
            "JOIN states s ON gr.state_id = s.id " +
            "WHERE  gr.flow_status = :flowStatus",
            nativeQuery = true)
    List<Object[]> getNhaReviewerPending(Integer flowStatus);

    @Query(value = """
    SELECT * FROM grant_requests
    
    """, nativeQuery = true)
    List<GrantRequests> findAllGrantRequestForReviwer();

    @Query(value = "SELECT COALESCE(SUM(released_amount), 0) FROM grant_requests WHERE state_id = :stateId", nativeQuery = true)
    BigDecimal getTotalReleasedAmountByStateId(Integer stateId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE grant_requests SET " +
            "sc_amount = :scAmount, " +
            "st_amount = :stAmount, " +
            "gc_amount = :gcAmount, " +
            "sanction_date = :sanctionDate, " +
            "sanction_letter = :sanctionLetter, "+
            "released_amount = :scAmount + :stAmount + :gcAmount , " +
            "flow_status = 9 " +
            "WHERE request_id = :requestId",
            nativeQuery = true)
    int updateGrantSanctionDetailsByRequestId(BigDecimal scAmount,
                                              BigDecimal stAmount,
                                              BigDecimal gcAmount,
                                              LocalDateTime sanctionDate, // or LocalDate if DB supports
                                              String requestId,
                                               byte[] sanctionLetter);

    @Modifying
    @Transactional
    @Query(value = "UPDATE grant_requests SET " +
            "e_sign_status_state_ceo = :status ," +
            "esign_txn_id = :txnId  ," +
            "esign_pdf = :esignLetter " +




            "WHERE request_id = :requestId",
            nativeQuery = true)
    int updateEsignStatusByRequestID(String requestId,
                                              byte[] esignLetter, String txnId, boolean status);


}
