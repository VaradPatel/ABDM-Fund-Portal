package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.Dashboard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IDashboardRepo extends JpaRepository<Dashboard, Integer> {
}
