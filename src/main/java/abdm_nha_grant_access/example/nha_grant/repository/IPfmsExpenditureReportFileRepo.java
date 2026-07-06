package abdm_nha_grant_access.example.nha_grant.repository;

import abdm_nha_grant_access.example.nha_grant.entity.PfmsExpenditureReportFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface IPfmsExpenditureReportFileRepo extends JpaRepository<PfmsExpenditureReportFile, Integer> {
    Optional<PfmsExpenditureReportFile> findByReportDateAndFinancialYearAndQuarter(
            LocalDate reportDate, String financialYear, String quarter);
}
