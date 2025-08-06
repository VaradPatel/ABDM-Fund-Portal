package nha_grant_access.example.nha_grant.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AashaImplementationTrust {

    private String stateName;  // UT Name
    private String modeOfImplementation;
    private String dateOfImplementation;
    private BigDecimal benefitCover;
    private BigDecimal nhaShareInGia;
    private String schemeName;
    private BigDecimal totalFamiliesCoveredInStateAsPerMou;
    private BigDecimal totalEligibleAshaAwwAwhFamilies;

    private BigDecimal maxGiaImplementationByNhaPerFamily;
    private BigDecimal maxGiaImplementationByNha;


    private String policyPeriod;

    // Section 2 - Financial Details
    private BigDecimal totalTreatmentCostPaidBySha;
    private BigDecimal nhaShareInCostForAshaAwsAww;
    private BigDecimal upfrontReleaseByShaForPmjay;
    private BigDecimal nhaShareCorrespondingToShaRelease;
    private BigDecimal paymentTrancheNo;
    private BigDecimal totalAmountPayableTillThisTranche;
    private BigDecimal totalAmountPayableByNhaAsOnDate;
    private BigDecimal earlierAmountReleasedByNha;
    private BigDecimal unspentAmountAsPerUc;
    private BigDecimal amountProposedToBeReleased;
}
