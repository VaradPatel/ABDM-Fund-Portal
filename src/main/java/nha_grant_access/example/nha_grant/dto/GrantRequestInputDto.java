package nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GrantRequestInputDto {
private String requestId;

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

    @NotNull(message = "pmjayBeneficiaryCount is mandatory")
    private Long pmjayBeneficiaryCount;

    @NotNull(message = "totalBeneficiaryCount is mandatory")
    private Long totalBeneficiaryCount;


    private BigDecimal premium;

    @NotNull(message = "nhaShare is mandatory")
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

    private Boolean bankMappedWithPfms=false;

    private Boolean eSignStatusStateCeo=false;
    private String remarks;
private String queryResponse;
    private Boolean positiveBalance=false;
    private String queryId;


}
