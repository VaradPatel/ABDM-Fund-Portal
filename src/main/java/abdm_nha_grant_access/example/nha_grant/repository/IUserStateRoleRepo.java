package abdm_nha_grant_access.example.nha_grant.repository;

import abdm_nha_grant_access.example.nha_grant.entity.UserStateRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IUserStateRoleRepo extends JpaRepository<UserStateRole, Integer> {
    @Query("SELECT usr FROM UserStateRole usr WHERE usr.user.id = :userId")
    List<UserStateRole> getUserStateRoleByUserid(Integer userId);
    @Query(value = "SELECT * FROM user_state_role WHERE role_id = :roleId AND state_id IN (:stateId)", nativeQuery = true)

    List<UserStateRole>getUserByStateAndRole(Integer roleId, List<Integer>stateId );

}
