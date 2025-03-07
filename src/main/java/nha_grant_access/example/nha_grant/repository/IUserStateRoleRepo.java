package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.Action;
import nha_grant_access.example.nha_grant.entity.UserStateRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IUserStateRoleRepo extends JpaRepository<UserStateRole, Integer> {
}
