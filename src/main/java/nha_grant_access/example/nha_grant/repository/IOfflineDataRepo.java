package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.ImplementTrustCalc;
import nha_grant_access.example.nha_grant.entity.OfflineData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IOfflineDataRepo extends JpaRepository<OfflineData, Integer> {
}
