package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IactionRepository extends JpaRepository<Action, Integer> {
}
