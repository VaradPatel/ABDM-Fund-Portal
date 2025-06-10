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
public class ProposalDetail {

@JsonProperty("ProposalType")
    private String proposalType;
@JsonProperty("PolicyPeriod")
    private String policyPeriod;
@JsonProperty("FinancialYear")
    private String financialYear;
@JsonProperty("ModeOfImplementation")
    private String modeOfImplementation;

@JsonProperty("InsuranceCompany")
    private String insuranceCompany;
@JsonProperty("PremiumInsuranceAmount")
    private String premiumInsuranceAmount;
}

