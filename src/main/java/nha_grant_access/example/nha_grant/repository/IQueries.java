package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.Action;
import nha_grant_access.example.nha_grant.entity.Queries;
import nha_grant_access.example.nha_grant.entity.WorkFlowConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IQueries extends JpaRepository<Queries, Integer> {

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

   }
