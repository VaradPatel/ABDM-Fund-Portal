package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardShaFin {

    private BigDecimal totalAmountRequested;
    private BigDecimal totalAmountReleased;
    private BigDecimal maximumEligibleGrant;
    private Integer completedProposals;
    private Integer pendingProposals;
    private Integer pendingQueries;
    private Integer respondedQueries;
    private Integer totalPendingQueryState;
}
