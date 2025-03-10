package nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

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

    @NotNull(message = "insuranceCompany is mandatory")
    private String insuranceCompany;

    @NotNull(message = "policyStartDate is mandatory")
    private LocalDate policyStartDate;

    @NotNull(message = "policyEndDate is mandatory")
    private LocalDate policyEndDate;

    @NotNull(message = "financialYear is mandatory")
    private String financialYear;

    @NotNull(message = "tranche is mandatory")
    private Integer tranche;

    @NotNull(message = "pmjayBeneficiaryCount is mandatory")
    private Long pmjayBeneficiaryCount;

    @NotNull(message = "totalBeneficiaryCount is mandatory")
    private Long totalBeneficiaryCount;

    @NotNull(message = "premium is mandatory")
    private BigDecimal premium;

    @NotNull(message = "nhaShare is mandatory")
    private BigDecimal nhaShare;

    @NotNull(message = "maxEligibleGrant is mandatory")
    private BigDecimal maxEligibleGrant;

    @NotNull(message = "releaseTillDate is mandatory")
    private BigDecimal releaseTillDate;

    @NotNull(message = "requestedAmount is mandatory")
    private BigDecimal requestedAmount;

    @NotNull(message = "stateShare is mandatory")
    private BigDecimal stateShare;


    private Boolean eSignStatusStateCeo;
}
