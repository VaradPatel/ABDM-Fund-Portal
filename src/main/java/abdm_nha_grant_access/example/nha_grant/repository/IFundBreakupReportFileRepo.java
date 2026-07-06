package abdm_nha_grant_access.example.nha_grant.repository;

import abdm_nha_grant_access.example.nha_grant.entity.FundBreakupReportFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface IFundBreakupReportFileRepo extends JpaRepository<FundBreakupReportFile, Integer> {
    Optional<FundBreakupReportFile> findByReportDateAndFinancialYearAndQuarter(
            LocalDate reportDate, String financialYear, String quarter);
}
