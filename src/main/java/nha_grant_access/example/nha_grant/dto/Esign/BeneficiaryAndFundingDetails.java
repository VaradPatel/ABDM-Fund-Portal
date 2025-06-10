package nha_grant_access.example.nha_grant.dto.Esign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BeneficiaryAndFundingDetails {
@JsonProperty("Tranche")
    private String Tranche;
    @JsonProperty("NumberOfPMJAYBeneficiaries")
    private String NumberOfPMJAYBeneficiaries;

    @JsonProperty("PremiumValue")
    private String PremiumValue;

    @JsonProperty("NHAShare")
    private String NHAShare;

    @JsonProperty("MaxEligibleGrant")
    private String MaxEligibleGrant;
    @JsonProperty("ReleasedAmountTillDate")
    private String ReleasedAmountTillDate;
    @JsonProperty("RequestedAmount")
    private String RequestedAmount;
}
