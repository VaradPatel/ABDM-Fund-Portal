package nha_grant_access.example.nha_grant.dto.Esign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ComplianceAndRequiredDocuments {

    private String isBankMappedWithPFMS;
    private String mandateFormAttached;
    private String claimPaidSummarySheetAttached;
    private String insurancePaymentReceiptAttached;
    private String provisionalUtilizationCertificateAttached;
    private String auditReportOfPreviousFinancialYearAttached;
    private String auditedUCOfPreviousFinancialYearAttached;
    private String auditedIncomeAndExpenditureSheetOfPreviousFinancialYearAttached;
    private String govtOrdersForUpdatedPremiumForPresentPolicyYearAttached;
    private String interestDepositedInBharatKoshAccountAttached;
    private String otherDocumentsAttached;
    private String openingBalanceInUCShouldBeAlwaysPositiveAndShouldBeMatchedWithPreviousYearClosingBalanceChecked;

}
