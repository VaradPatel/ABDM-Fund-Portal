package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.ProposalType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IProposalTypesRepo extends JpaRepository<ProposalType, Integer> {
}
