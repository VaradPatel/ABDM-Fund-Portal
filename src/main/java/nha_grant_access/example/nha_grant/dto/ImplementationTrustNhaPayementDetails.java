package nha_grant_access.example.nha_grant.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImplementationTrustNhaPayementDetails {
    private String stateName;
    private String modeOfImplementation;
    private String dateOfImplementation;
    private BigDecimal benefitCover;
    private BigDecimal nhaShareInGia;
    private String schemeName;
    private BigDecimal totalPopulationCoveredAsPerMou;
    private BigDecimal eligibleSeccPopulation;
    private BigDecimal percentageEligibleSeccPopulation;
    private BigDecimal maxGiaImplementationPerFamily;
    private BigDecimal maxGiaAdminPerFamily;
    private BigDecimal maxGiaImplementationByNha;
    private BigDecimal maxGiaAdminByNha;
    private String policyPeriod;

    // Section 2 - Financial Details
    private BigDecimal totalTreatmentCostPaidBySha;
    private BigDecimal treatmentCostForPmjayBeneficiaries;
    private BigDecimal nhaShareInPmjayTreatmentCost;
    private BigDecimal upfrontReleaseByShaForPmjay;
    private BigDecimal nhaShareCorrespondingToShaRelease;
    private BigDecimal paymentTrancheNo;
    private BigDecimal totalAmountPayableTillThisTranche;
    private BigDecimal totalAmountPayableByNhaAsOnDate;
    private BigDecimal earlierAmountReleasedByNha;
    private BigDecimal unspentAmountAsPerUc;
    private BigDecimal amountProposedToBeReleased;
}
