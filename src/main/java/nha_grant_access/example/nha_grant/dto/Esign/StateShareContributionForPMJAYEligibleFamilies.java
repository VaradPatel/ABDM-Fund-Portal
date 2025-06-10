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
public class StateShareContributionForPMJAYEligibleFamilies {
    @JsonProperty("Amount1")
    private String amount1;

    @JsonProperty("DepositDate1")
    private String depositDate1;

    @JsonProperty("Amount2")
    private String amount2;

    @JsonProperty("DepositDate2")
    private String depositDate2;

    @JsonProperty("Amount3")
    private String amount3;

    @JsonProperty("DepositDate3")
    private String depositDate3;

    @JsonProperty("Amount4")
    private String amount4;

    @JsonProperty("DepositDate4")
    private String depositDate4;

    @JsonProperty("Amount5")
    private String amount5;

    @JsonProperty("DepositDate5")
    private String depositDate5;

    @JsonProperty("Amount6")
    private String amount6;

    @JsonProperty("DepositDate6")
    private String depositDate6;

    @JsonProperty("Amount7")
    private String amount7;

    @JsonProperty("DepositDate7")
    private String depositDate7;

    @JsonProperty("Amount8")
    private String amount8;

    @JsonProperty("DepositDate8")
    private String depositDate8;

    @JsonProperty("Amount9")
    private String amount9;

    @JsonProperty("DepositDate9")
    private String depositDate9;

    @JsonProperty("Amount10")
    private String amount10;

    @JsonProperty("DepositDate10")
    private String depositDate10;

    @JsonProperty("TotalAmount")
    private String totalAmount;

    @JsonProperty("BankStatementAttached")
    private String bankStatementAttached;
}
