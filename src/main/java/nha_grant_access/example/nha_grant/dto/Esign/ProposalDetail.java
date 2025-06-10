package nha_grant_access.example.nha_grant.dto.Esign;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProposalDetail {

    private String proposalType;
    private String policyPeriod;
    private String financialYear;
    private String modeOfImplementation;
    private String insuranceCompany;
    private String premiumInsuranceAmount;
}

