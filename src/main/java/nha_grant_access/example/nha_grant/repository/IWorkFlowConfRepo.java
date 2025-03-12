package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.WorkFlowConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkFlowConfRepo extends JpaRepository<WorkFlowConfiguration, Integer> {
}
