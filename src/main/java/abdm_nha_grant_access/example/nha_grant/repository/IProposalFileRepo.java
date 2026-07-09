package abdm_nha_grant_access.example.nha_grant.repository;

import abdm_nha_grant_access.example.nha_grant.entity.ProposalFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IProposalFileRepo extends JpaRepository<ProposalFile, Integer> {

    List<ProposalFile> findByProposalId(Integer proposalId);

    List<ProposalFile> findByProposalIdIn(List<Integer> proposalIds);
}
