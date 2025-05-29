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
public class NhaReviewerDashboard {
    private BigDecimal totalRequestedAmount;
    private BigDecimal totalReleasedAmount;
    private Integer pending;

    private Integer accepted;

    private Integer pendingQuery;
    private Integer queryRaised;
    private Integer resolvedQuery;
    private BigDecimal totalMaxEligibleGrants;
}
