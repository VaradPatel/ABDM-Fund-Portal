package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.States;
import nha_grant_access.example.nha_grant.entity.StatusDescription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IStatusDescription extends JpaRepository<StatusDescription, Integer> {
}
