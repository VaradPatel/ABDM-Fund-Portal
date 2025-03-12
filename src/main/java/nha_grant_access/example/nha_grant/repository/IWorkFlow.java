package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.UserStateRole;
import nha_grant_access.example.nha_grant.entity.WorkFlow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IWorkFlow extends JpaRepository<WorkFlow, Integer> {
}
