package abdm_nha_grant_access.example.nha_grant.repository;

import abdm_nha_grant_access.example.nha_grant.entity.ProposalWorkflowHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProposalWorkflowHistoryRepo extends JpaRepository<ProposalWorkflowHistory, Integer> {

    List<ProposalWorkflowHistory> findByProposalIdOrderByCreatedAtAsc(Integer proposalId);
}
