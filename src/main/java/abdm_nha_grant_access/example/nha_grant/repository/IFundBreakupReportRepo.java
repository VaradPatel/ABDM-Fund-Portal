package abdm_nha_grant_access.example.nha_grant.repository;

import abdm_nha_grant_access.example.nha_grant.entity.FundBreakupReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IFundBreakupReportRepo extends JpaRepository<FundBreakupReport, Integer> {
    Optional<FundBreakupReport> findByReportDateAndStateId(LocalDate reportDate, Integer stateId);

    List<FundBreakupReport> findByStateIdInAndReportDateIn(List<Integer> stateIds, List<LocalDate> reportDates);

    List<FundBreakupReport> findByStateIdInAndReportDateBetween(List<Integer> stateIds, LocalDate startDate, LocalDate endDate);

    @Query("SELECT DISTINCT f.reportDate FROM FundBreakupReport f WHERE f.stateId IN :stateIds ORDER BY f.reportDate DESC")
    List<LocalDate> findDistinctReportDatesByStateIdInOrderByReportDateDesc(@Param("stateIds") List<Integer> stateIds);
}
