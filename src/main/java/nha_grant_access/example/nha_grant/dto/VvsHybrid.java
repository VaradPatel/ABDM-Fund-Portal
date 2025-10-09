package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class VvsHybrid {

    private String stateName;
    private String modeOfImplementation;
    private String dateOfImplementation;
    private BigDecimal benefitCover;
    private BigDecimal nhaShareInGia;
    private String schemeName;
    private BigDecimal newBeneficiaryInstate;
    private BigDecimal oldBeneficiaryInState;
    private BigDecimal maxGiaImplementationPerFamilyNew;
    private BigDecimal maxGiaImplementationPerFamilyOld;

    private BigDecimal maxGiaImplementationByNha;


    private String nameOfInsuranceCompany;
    private BigDecimal annualInsurancePremiumFamily;
    private BigDecimal nhaShareOfPremiumPayable;
    private BigDecimal shaShareOfPremiumPayable;

    private BigDecimal upfrontReleaseByShaForPmjay;
    private BigDecimal nhaShareCorrespondingToShaRelease;
    private BigDecimal paymentTrancheNo;
    private BigDecimal totalAmountPayableTillThisTranche;
    private BigDecimal totalAmountPayableByNhaAsOnDate;
    private BigDecimal earlierAmountReleasedByNha;
    private BigDecimal unspentAmountAsPerUc;
    private BigDecimal amountProposedToBeReleased;
}
