package nha_grant_access.example.nha_grant.repository;

import jakarta.transaction.Transactional;
import nha_grant_access.example.nha_grant.entity.Action;
import nha_grant_access.example.nha_grant.entity.Queries;
import nha_grant_access.example.nha_grant.entity.WorkFlowConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IQueries extends JpaRepository<Queries, Integer> {
//Optional<Queries> findById(String queryId);
   @Query(value = "SELECT " +
           "gr.request_id, " +
           "gr.requested_amount, " +
           "gr.created_at, " +
           "q.query_comment, " +
           "q.query_response_comment " +
           "FROM grant_requests gr " +
           "JOIN queries q ON gr.request_id = q.request_id " +
           "WHERE gr.state_id = :stateId AND q.active = false",
           nativeQuery = true)
   List<Object[]> findActiveQueriesShaFinance( Integer stateId);

   @Query(value = "SELECT DISTINCT wf.remarks, r.name, wf.created_at " +
           "FROM work_flow wf " +
           "JOIN user_state_role usr ON wf.user_id = usr.user_id " +
           "JOIN roles r ON r.id = usr.role_id " +
           "WHERE wf.request_id = :requestId " +
           "AND wf.action_id IN (2, 7) " +
           "ORDER BY wf.created_at",
           nativeQuery = true)
   List<Object[]> getWorkflowRemarksByRequestId( String requestId);

   @Modifying
   @Transactional
   @Query(value = "UPDATE queries SET active = false WHERE request_id = :requestId", nativeQuery = true)
   int deactivateQueriesByRequestId(String requestId);


}

