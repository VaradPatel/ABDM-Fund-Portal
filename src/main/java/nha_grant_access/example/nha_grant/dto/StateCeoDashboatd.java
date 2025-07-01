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
public class StateCeoDashboatd {
    private BigDecimal totalRequestedAmount;
    private BigDecimal totalReleasedAmount;
    private Long nhaReview;
    private Long pendingProposals;
    private Long pendingQuery;
    private Long shaPending;
    private Long resolvedQuery;
    private BigDecimal maxEligibleGrant;
}
