package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.WorkFlowConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IWorkFlowConfRepo extends JpaRepository<WorkFlowConfiguration, Integer> {

    @Query(value = """
        SELECT * FROM work_flow_configuration 
        WHERE action_performed = :actionId 
        AND action_performed_by = :roleId
    """, nativeQuery = true)
    WorkFlowConfiguration findByActionPerformedIdAndActionPerformedById(Integer actionId, Integer roleId);

}
