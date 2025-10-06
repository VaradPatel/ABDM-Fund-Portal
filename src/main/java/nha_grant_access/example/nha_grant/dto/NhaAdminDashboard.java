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


        private BigDecimal onlineRequestedAmount;
        private BigDecimal onlineReleasedAmount;
        private Integer approved;
        private Integer accepted;
        private Integer review;
        private Integer deactivatedUsers;
        private Integer verifiedUsers;
        private Integer pendingUsers;
        private BigDecimal totalMaxEligibleGrants;
        private BigDecimal onlineReleaseAmountGc;
        private BigDecimal onlineReleaseAmountSc;
        private BigDecimal onlineReleaseAmountSt;

        private BigDecimal offlineRequestedAmount;
        private BigDecimal offlineReleasedAmount;
        private BigDecimal offlineReleaseAmountGc;
        private BigDecimal offineReleaseAmountSc;
        private BigDecimal offlineReleaseAmountSt;

        private BigDecimal TotalQ1Released;
        private BigDecimal TotalQ1Perc;

        private BigDecimal TotalQ2Released;
        private BigDecimal TotalQ2Perc;

        private BigDecimal TotalQ3Released;
        private BigDecimal TotalQ3Perc;

        private BigDecimal TotalQ4Released;
        private BigDecimal TotalQ4Perc;





    }


