package nha_grant_access.example.nha_grant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfflineProposalRequest {
    @NotNull(message = "stateId is mandatory")
    private Integer stateId;
    @NotNull(message = "proposalTypeId is mandatory")
    private Integer proposalTypeId;

    @NotNull(message = "implementationModeId is mandatory")
    private Integer implementationModeId;
    @NotNull(message = "policyStartDate is mandatory")
    private LocalDate policyStartDate;

    @NotNull(message = "policyEndDate is mandatory")
    private LocalDate policyEndDate;

    @NotNull(message = "financialYear is mandatory")
    private String financialYear;

    @NotNull(message = "tranche is mandatory")
    private List<Integer> tranche;
    private Integer schemeId;
    private String schemeName;
    @NotNull(message = "requestedAmount is mandatory")
    private BigDecimal requestedAmount;

    private BigDecimal amountSC;
    private BigDecimal amountST;
    private BigDecimal amountGC;

    private BigDecimal releasedAmount;
    private LocalDate requestedDate;
    private LocalDate ReleasedDate;

}
