package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.States;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IstatesRepository extends JpaRepository<States, Integer> {
}
