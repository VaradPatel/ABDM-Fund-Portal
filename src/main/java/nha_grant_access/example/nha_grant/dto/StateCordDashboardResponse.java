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

public class StateCordDashboardResponse {
    private BigDecimal totalRequestedAmount;
    private BigDecimal totalReleasedAmount;
    private Integer pending;
    private Integer pendingForSanction;
    private Integer pendingAtNhareviewer;
    private Integer sanctionUpload;
    private Integer pendingQuery;
    private Integer queryRaised;
    private Integer resolvedQuery;
}
