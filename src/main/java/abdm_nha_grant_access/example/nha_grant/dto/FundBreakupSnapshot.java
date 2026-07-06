package abdm_nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundBreakupSnapshot {
    private LocalDate reportDate;
    private BigDecimal totalFundReleased;
    private BigDecimal totalExpenditure;
    private BigDecimal totalBalance;
    private BigDecimal hrFundReleased;
    private BigDecimal hrExpenditure;
    private BigDecimal hrBalance;
    private BigDecimal iecCbFundReleased;
    private BigDecimal iecCbExpenditure;
    private BigDecimal iecCbBalance;
    private BigDecimal adminFundReleased;
    private BigDecimal adminExpenditure;
    private BigDecimal adminBalance;
}
