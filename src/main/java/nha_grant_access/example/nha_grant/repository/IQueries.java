package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.Action;
import nha_grant_access.example.nha_grant.entity.Queries;
import nha_grant_access.example.nha_grant.entity.WorkFlowConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface IQueries extends JpaRepository<Queries, Integer> {
   }
