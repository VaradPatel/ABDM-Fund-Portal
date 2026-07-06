package abdm_nha_grant_access.example.nha_grant.repository;

import abdm_nha_grant_access.example.nha_grant.entity.PfmsExpenditureReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IPfmsExpenditureReportRepo extends JpaRepository<PfmsExpenditureReport, Integer> {
    Optional<PfmsExpenditureReport> findByReportDateAndStateId(LocalDate reportDate, Integer stateId);

    List<PfmsExpenditureReport> findByStateIdInAndReportDateIn(List<Integer> stateIds, List<LocalDate> reportDates);

    List<PfmsExpenditureReport> findByStateIdInAndReportDateBetween(List<Integer> stateIds, LocalDate startDate, LocalDate endDate);

    @Query("SELECT DISTINCT p.reportDate FROM PfmsExpenditureReport p WHERE p.stateId IN :stateIds ORDER BY p.reportDate DESC")
    List<LocalDate> findDistinctReportDatesByStateIdInOrderByReportDateDesc(@Param("stateIds") List<Integer> stateIds);
}
