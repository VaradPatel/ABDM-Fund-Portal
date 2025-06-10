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
public class ComplianceAndRequiredDocuments {
@JsonProperty("IsBankMappedWithPFMS")
    private String isBankMappedWithPFMS;
@JsonProperty("MandateFormAttached")
    private String mandateFormAttached;
@JsonProperty("ClaimPaidSummarySheetAttached")
    private String claimPaidSummarySheetAttached;

    @JsonProperty("InsurancePaymentReceiptAttached")
    private String insurancePaymentReceiptAttached;
    @JsonProperty("ProvisionalUtilizationCertificateAttached")
    private String provisionalUtilizationCertificateAttached;

    @JsonProperty("AuditReportOfPreviousFinancialYearAttached")
    private String auditReportOfPreviousFinancialYearAttached;

    @JsonProperty("AuditedUCOfPreviousFinancialYearAttached")
    private String auditedUCOfPreviousFinancialYearAttached;

    @JsonProperty("AuditedIncomeAndExpenditureSheetOfPreviousFinancialYearAttached")
    private String auditedIncomeAndExpenditureSheetOfPreviousFinancialYearAttached;

    @JsonProperty("GovtOrdersForUpdatedPremiumForPresentPolicyYearAttached")
    private String govtOrdersForUpdatedPremiumForPresentPolicyYearAttached;

    @JsonProperty("InterestDepositedInBharatKoshAccountAttached")
    private String interestDepositedInBharatKoshAccountAttached;

    @JsonProperty("OtherDocumentsAttached")
    private String otherDocumentsAttached;

    @JsonProperty("OpeningBalanceInUCShouldBeAlwaysPositiveAndShouldBeMatchedWithPreviousYearClosingBalanceChecked")
    private String openingBalanceInUCShouldBeAlwaysPositiveAndShouldBeMatchedWithPreviousYearClosingBalanceChecked;

}
