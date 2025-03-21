package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.Action;
import nha_grant_access.example.nha_grant.entity.UserStateRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IUserStateRoleRepo extends JpaRepository<UserStateRole, Integer> {
    @Query("SELECT usr FROM UserStateRole usr WHERE usr.user.id = :userId")
    List<UserStateRole> getUserStateRoleByUserid(Integer userId);
}
