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
public class NhaAdminDashboard {


        private BigDecimal totalRequestedAmount;
        private BigDecimal totalReleasedAmount;
        private Integer approved;
        private Integer accepted;
        private Integer review;
        private Integer deactivatedUsers;
        private Integer verifiedUsers;
        private Integer pendingUsers;
        private BigDecimal totalMaxEligibleGrants;
    }


