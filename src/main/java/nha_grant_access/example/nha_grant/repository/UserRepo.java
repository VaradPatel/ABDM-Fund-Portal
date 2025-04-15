package nha_grant_access.example.nha_grant.repository;

import jakarta.transaction.Transactional;
import nha_grant_access.example.nha_grant.entity.User;
import org.hibernate.mapping.Selectable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    @Query(value="Select u.* from users u where u.email= :email",nativeQuery = true)
    Optional<User> findByEmail(String email);
    Optional<User> findByMobileNumber(String mobileNumber);
    @Query(value="Select * from users u where u.mobile_number= :mobileNumber and u.is_verified=true",nativeQuery = true)
    Optional<User>findActiveByMobile(String mobileNumber);
    @Query(value = "SELECT u.id, u.email, u.mobile_number, u.designation, s.name AS state_name, r.name AS role_name , u.name, u.created_at , u.is_verified " +
            "FROM users u " +
            "JOIN user_state_role usr ON u.id = usr.user_id " +
            "JOIN states s ON usr.state_id = s.id " +
            "JOIN roles r ON u.role_id = r.id " +
            "WHERE usr.state_id = :stateId " +
            "AND u.role_id = 1 order by u.is_verified ASC, u.created_at desc",
            nativeQuery = true)



    List<Object[]> findUsersRequestByStateId(Integer stateId);
    @Query(value = "SELECT u.*  FROM users u JOIN user_state_role usr ON u.id = usr.user_id WHERE  u.role_id <> 1 and u.is_verified=false", nativeQuery = true)
    List<User> findUsersRequestByAdmin();


    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET is_verified = :isApproved, password = :password WHERE id = :userId",
            nativeQuery = true)
    int updateUserVerificationStatus(
            Integer userId,
            Boolean isApproved,
           String password
    );

    @Transactional
    @Modifying
    @Query(value = "UPDATE users SET is_verified = :isApproved, password = :password WHERE mobile_number = :mobileNumber",
            nativeQuery = true)
    int updateUserVerificationStatusByMobile(
            String mobileNumber,
            Boolean isApproved,
            String password
    );




}
