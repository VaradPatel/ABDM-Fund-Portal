package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

}
