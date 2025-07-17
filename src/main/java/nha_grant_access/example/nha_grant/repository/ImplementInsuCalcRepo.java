package nha_grant_access.example.nha_grant.repository;

import nha_grant_access.example.nha_grant.entity.ImplementInsuCalc;
import nha_grant_access.example.nha_grant.entity.ImplementTrustCalc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImplementInsuCalcRepo extends JpaRepository<ImplementInsuCalc, Integer> {
}
