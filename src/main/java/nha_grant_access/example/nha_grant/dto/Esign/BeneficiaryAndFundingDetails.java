package nha_grant_access.example.nha_grant.dto.Esign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BeneficiaryAndFundingDetails {
    private String tranche;
    private String numberOfPMJAYBeneficiaries;
    private String premiumValue;
    private String nHAShare;
    private String maxEligibleGrant;
    private String releasedAmountTillDate;
    private String requestedAmount;
}
