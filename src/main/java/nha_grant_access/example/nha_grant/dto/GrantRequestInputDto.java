package nha_grant_access.example.nha_grant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class  GrantRequestInputDto {


    private ImplementationTrustNhaPayementDetails implementationTrustNhaPayementDetails;
    private AdminNhaPaymentDetails adminNhaPaymentDetails;
    private ImplementationInsurancePayementDetails implementationInsurancePayementDetails;
    private  VVSImplementationNewBenef vvsImplementationNewBenef;
    private AashaImplementationTrust aashaImplementationTrust;

    @NotNull(message = "stateId is mandatory")
    private Integer stateId;

    @NotNull(message = "userId is mandatory")
    private Integer userId;

    @NotNull(message = "proposalTypeId is mandatory")
    private Integer proposalTypeId;

    @NotNull(message = "implementationModeId is mandatory")
    private Integer implementationModeId;


    private String insuranceCompany;


    @NotNull(message = "policyStartDate is mandatory")
    private LocalDate policyStartDate;

    @NotNull(message = "policyEndDate is mandatory")
    private LocalDate policyEndDate;

    @NotNull(message = "financialYear is mandatory")
    private String financialYear;

    @NotNull(message = "tranche is mandatory")
    private List<Integer> tranche;


    private Long pmjayBeneficiaryCount= 0L;


    private Long totalBeneficiaryCount= 0L;


    private BigDecimal premium;

//    @NotNull(message = "nhaShare is mandatory")
    private BigDecimal nhaShare;

    @NotNull(message = "maxEligibleGrant is mandatory")
    private BigDecimal maxEligibleGrant;

    @NotNull(message = "releaseTillDate is mandatory")
    private BigDecimal releaseTillDate;

    @NotNull(message = "requestedAmount is mandatory")
    private BigDecimal requestedAmount;

//    @NotNull(message = "stateShare is mandatory")
//    private BigDecimal stateShare;
 // Specifies JSONB type
private List<StateShare> stateShare;
@NotNull(message="totalStateShare cannot be null")
private BigDecimal totalStateShare;

private Integer schemeId;
private String schemeName;

    private Boolean bankMappedWithPfms=false;

    private Boolean eSignStatusStateCeo=false;
    private String remarks;

    @Pattern(
            regexp = "^(?!.*<[^>]+>)[a-zA-Z0-9()\\-_,.?\\s]*$",
            message = "Input must not contain HTML tags and may only include letters, numbers, spaces, parentheses, hyphen, underscore, period, comma, and question mark"
    )
    private String queryResponse;
    private Boolean positiveBalance;
    private Integer queryId;
    @JsonProperty("requestId")
    private String requestId;

    private BigDecimal totalFamiliesCoveredInStateAsPerMou;
    private BigDecimal totalEligibleAshaAwwAwhFamilies;

    private BigDecimal newBeneficiaryInstate;
    private BigDecimal oldBeneficiaryInState;


}
