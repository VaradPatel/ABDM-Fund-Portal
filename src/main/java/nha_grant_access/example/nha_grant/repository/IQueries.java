package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.Action;
import nha_grant_access.example.nha_grant.entity.Queries;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IQueries extends JpaRepository<Queries, Integer> {
}
