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
public class GIARequestDetails {
    @JsonProperty("ProposalDetail")
    private ProposalDetail proposalDetail;

    @JsonProperty("BeneficiaryAndFundingDetails")
    private BeneficiaryAndFundingDetails beneficiaryAndFundingDetails;

    @JsonProperty("StateShareContributionForPMJAYEligibleFamilies")
    private StateShareContributionForPMJAYEligibleFamilies stateShareContributionForPMJAYEligibleFamilies;

    @JsonProperty("ComplianceAndRequiredDocuments")
    private ComplianceAndRequiredDocuments complianceAndRequiredDocuments;

    @JsonProperty("ClaimPaidSummarySheetAdminExpenditureSheetHasBeenEnclosedChecked")
    private String ClaimPaidSummarySheetAdminExpenditureSheetHasBeenEnclosedChecked;
    @JsonProperty("MandateFormIsEnclosedChecked")
    private String MandateFormIsEnclosedChecked;
    @JsonProperty("ProvisionalUtilizationCertificateAndAuditedUtilizationCertificateForTheCentralShareAreEnclosedWithAPositiveOpeningAndClosingBalanceChecked")
    private String provisionalUtilizationCertificateAndAuditedUtilizationCertificateForTheCentralShareAreEnclosedWithAPositiveOpeningAndClosingBalanceChecked;
    @JsonProperty("TheOpeningBalanceOfTheUtilizationCertificateMatchesTheClosingBalanceOfThePreviousFinancialYearChecked")
    private String TheOpeningBalanceOfTheUtilizationCertificateMatchesTheClosingBalanceOfThePreviousFinancialYearChecked;
    @JsonProperty("InterestDepositCertificateReceiptAmountIsMentionedInTheUtilizationCertificateIfApplicableChecked")
    private String InterestDepositCertificateReceiptAmountIsMentionedInTheUtilizationCertificateIfApplicableChecked;
    @JsonProperty("ReceiptAndPaymentAccountAndIncomeAndExpenditureAccountAreEnclosedShowingTheSeparateCentralShareAndAreInAgreementWithTheAuditedUtilizationCertificateChecked")
    private String ReceiptAndPaymentAccountAndIncomeAndExpenditureAccountAreEnclosedShowingTheSeparateCentralShareAndAreInAgreementWithTheAuditedUtilizationCertificateChecked;
    @JsonProperty("AuditedFinancialAccountsReportIsAccompaniedByTheAuditReportLetterChecked")
    private String AuditedFinancialAccountsReportIsAccompaniedByTheAuditReportLetterChecked;
    @JsonProperty("ComplianceActionTakenNotesAreEnclosedIfApplicableChecked")
    private String ComplianceActionTakenNotesAreEnclosedIfApplicableChecked;
    @JsonProperty("CopyOfTheChallanTowardsTheDepositOfInterestToCFIThroughTheBharatKoshPortalIsEnclosedIfApplicableChecked")
    private String CopyOfTheChallanTowardsTheDepositOfInterestToCFIThroughTheBharatKoshPortalIsEnclosedIfApplicableChecked;
}


