package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.ImplementationTrustNhaPayementDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class CalculationService {

    public ImplementationTrustNhaPayementDetails implementationTruestCalc(ImplementationTrustNhaPayementDetails details)
    {

//             implementationNhaPayementDetails.setPercentageEligibleSeccPopulation(implementationNhaPayementDetails.getEligibleSeccPopulation()/implementationNhaPayementDetails.getTotalPopulationCoveredAsPerMou());
//             implementationNhaPayementDetails.setMaxGiaImplementationPerFamily(1052*(implementationNhaPayementDetails.getNhaShareInGia()));
//             implementationNhaPayementDetails.setMaxGiaAdminPerFamily(50*(implementationNhaPayementDetails.getNhaShareInGia()));
//             implementationNhaPayementDetails.setMaxGiaImplementationByNha(implementationNhaPayementDetails.getEligibleSeccPopulation()*implementationNhaPayementDetails.getMaxGiaImplementationPerFamily());
//             implementationNhaPayementDetails.setMaxGiaAdminByNha(implementationNhaPayementDetails.getEligibleSeccPopulation()*implementationNhaPayementDetails.getMaxGiaAdminPerFamily());
//
//
//             implementationNhaPayementDetails.setTreatmentCostForPmjayBeneficiaries(implementationNhaPayementDetails.getTotalTreatmentCostPaidBySha()*implementationNhaPayementDetails.getPercentageEligibleSeccPopulation());
//             implementationNhaPayementDetails.setNhaShareInPmjayTreatmentCost(implementationNhaPayementDetails.getTreatmentCostForPmjayBeneficiaries()*implementationNhaPayementDetails.getNhaShareInGia());
//             implementationNhaPayementDetails.setNhaShareCorrespondingToShaRelease(implementationNhaPayementDetails.getUpfrontReleaseByShaForPmjay()*(implementationNhaPayementDetails.getNhaShareInGia()/(1-implementationNhaPayementDetails.getNhaShareInGia())));
//             implementationNhaPayementDetails.setTotalAmountPayableTillThisTranche(implementationNhaPayementDetails.getMaxGiaImplementationByNha()*implementationNhaPayementDetails.getPaymentTrancheNo());
//
//        double minValue = Math.min(
//                Math.min(implementationNhaPayementDetails.getMaxGiaImplementationByNha(),implementationNhaPayementDetails.getTreatmentCostForPmjayBeneficiaries() ),
//                Math.min(implementationNhaPayementDetails.getNhaShareCorrespondingToShaRelease(),implementationNhaPayementDetails.getTotalAmountPayableTillThisTranche())
//        );
//        implementationNhaPayementDetails.setAmountProposedToBeReleased(minValue-implementationNhaPayementDetails.getEarlierAmountReleasedByNha()-implementationNhaPayementDetails.getUnspentAmountAsPerUc());
//        return implementationNhaPayementDetails;
        if (details.getTotalPopulationCoveredAsPerMou() != null && details.getTotalPopulationCoveredAsPerMou().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = details.getEligibleSeccPopulation()
                    .divide(details.getTotalPopulationCoveredAsPerMou(), 10, RoundingMode.HALF_UP);
            details.setPercentageEligibleSeccPopulation(percentage);
        }
        BigDecimal baseAdmin = BigDecimal.valueOf(50);
        BigDecimal population = details.getEligibleSeccPopulation();

        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            baseAdmin = BigDecimal.valueOf(150);

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            baseAdmin = BigDecimal.valueOf(200);
        }
        // max GIA implementation/admin per family
        BigDecimal maxImplPerFamily = new BigDecimal("1052").multiply(details.getNhaShareInGia());

        BigDecimal maxAdminPerFamily = baseAdmin.multiply(details.getNhaShareInGia());
        details.setMaxGiaImplementationPerFamily(maxImplPerFamily);
        details.setMaxGiaAdminPerFamily(maxAdminPerFamily);

        // max GIA by NHA
        BigDecimal maxImplByNha = details.getEligibleSeccPopulation().multiply(maxImplPerFamily);
        BigDecimal maxAdminByNha = details.getEligibleSeccPopulation().multiply(maxAdminPerFamily);
        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("20000000"));

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("10000000"));
        }
        else {
            maxAdminByNha = maxAdminByNha.max(new BigDecimal("150000000"));
        }

        details.setMaxGiaImplementationByNha(maxImplByNha);
        details.setMaxGiaAdminByNha(maxAdminByNha);
        // treatment cost for PM-JAY
        BigDecimal treatmentCostForPmjay = details.getTotalTreatmentCostPaidBySha()
                .multiply(details.getPercentageEligibleSeccPopulation());

        details.setTreatmentCostForPmjayBeneficiaries(treatmentCostForPmjay);

        // NHA's share in PM-JAY treatment
        BigDecimal nhaShareInPmjay = treatmentCostForPmjay.multiply(details.getNhaShareInGia());
        details.setNhaShareInPmjayTreatmentCost(nhaShareInPmjay);

        // NHA share corresponding to SHA release
        BigDecimal oneMinusShare = BigDecimal.ONE.subtract(details.getNhaShareInGia());
        BigDecimal nhaShareCorresponding;
        if(details.getNhaShareInGia().equals(BigDecimal.ONE))
        {
            nhaShareCorresponding= nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay()
                    .multiply(details.getNhaShareInGia());

        }
        else {
            nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay()
                    .multiply(details.getNhaShareInGia().divide(oneMinusShare, 10, RoundingMode.HALF_UP));
        }
        details.setNhaShareCorrespondingToShaRelease(nhaShareCorresponding);

        // total amount payable till this tranche
        BigDecimal totalTillTranche = maxImplByNha.multiply(details.getPaymentTrancheNo());
        details.setTotalAmountPayableTillThisTranche(totalTillTranche);
        BigDecimal min;
        if(details.getNhaShareInGia().equals(BigDecimal.ONE)) {
            min=minOfThree(
                    maxImplByNha,
                    nhaShareInPmjay,
                    totalTillTranche
            );

        }
        else
        {
            min = minOfFour(
                    maxImplByNha,
                    nhaShareInPmjay,
                    nhaShareCorresponding,
                    totalTillTranche
            );
        }


details.setTotalAmountPayableByNhaAsOnDate(min);

        // final amount to be released
        BigDecimal finalAmount = min
                .subtract(details.getEarlierAmountReleasedByNha())
                .subtract(details.getUnspentAmountAsPerUc());

        details.setAmountProposedToBeReleased(finalAmount);

        return details;
    }
    public ImplementationInsurancePayementDetails implementInsuCalc(ImplementationInsurancePayementDetails details)
    {
        if (details.getTotalPopulationCoveredAsPerMou() != null && details.getTotalPopulationCoveredAsPerMou().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = details.getEligibleSeccPopulation()
                    .divide(details.getTotalPopulationCoveredAsPerMou(), 10, RoundingMode.HALF_UP);
            details.setPercentageEligibleSeccPopulation(percentage);
        }



        // max GIA implementation/admin per family
        BigDecimal baseAdmin = BigDecimal.valueOf(50);
        BigDecimal population = details.getEligibleSeccPopulation();

        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            baseAdmin = BigDecimal.valueOf(150);

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            baseAdmin = BigDecimal.valueOf(200);
        }
        // max GIA implementation/admin per family
        BigDecimal maxImplPerFamily = new BigDecimal("1052").multiply(details.getNhaShareInGia());

        BigDecimal maxAdminPerFamily = baseAdmin.multiply(details.getNhaShareInGia());
        details.setMaxGiaImplementationPerFamily(maxImplPerFamily);
        details.setMaxGiaAdminPerFamily(maxAdminPerFamily);

        // max GIA by NHA
        BigDecimal maxImplByNha = details.getEligibleSeccPopulation().multiply(maxImplPerFamily);
        BigDecimal maxAdminByNha = details.getEligibleSeccPopulation().multiply(maxAdminPerFamily);
        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("20000000"));

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("10000000"));
        }
        else {
            maxAdminByNha = maxAdminByNha.max(new BigDecimal("150000000"));
        }

        details.setMaxGiaImplementationByNha(maxImplByNha);
        details.setMaxGiaAdminByNha(maxAdminByNha);

        BigDecimal baseAmount = new BigDecimal("1052");
if(details.getAnnualInsurancePremiumFamily().compareTo(BigDecimal.valueOf(1052)) > 0) {
    BigDecimal result = baseAmount
            .multiply(details.getNhaShareInGia())
            .multiply(details.getEligibleSeccPopulation());

    details.setNhaShareOfPremiumPayable(result);
}
else {
    BigDecimal result=details.getAnnualInsurancePremiumFamily().multiply(details.getEligibleSeccPopulation()).multiply(details.getNhaShareInGia());
details.setNhaShareOfPremiumPayable(result);

}
        BigDecimal shaShare = BigDecimal.ZERO;

        if (details.getAnnualInsurancePremiumFamily().compareTo(BigDecimal.valueOf(1052)) > 0) {
            shaShare = details.getAnnualInsurancePremiumFamily()
                    .subtract(details.getMaxGiaImplementationPerFamily())
                    .multiply(details.getEligibleSeccPopulation());
        } else {
            BigDecimal nhaShareInGia = details.getNhaShareInGia();
            BigDecimal oneMinusNhaShare = BigDecimal.ONE.subtract(nhaShareInGia);

            shaShare = details.getAnnualInsurancePremiumFamily()
                    .multiply(oneMinusNhaShare)
                    .multiply(details.getEligibleSeccPopulation());
        }

        details.setShaShareOfPremiumPayable(shaShare);
        if (details.getShaShareOfPremiumPayable() != null &&
                details.getShaShareOfPremiumPayable().compareTo(BigDecimal.ZERO) != 0) {


            BigDecimal re;
            if(details.getNhaShareInGia().equals(BigDecimal.ONE))
            {
                re=details.getUpfrontReleaseByShaForPmjay().multiply(details.getNhaShareInGia());
            }
            else {
                re = details.getUpfrontReleaseByShaForPmjay()
                        .multiply(details.getNhaShareOfPremiumPayable())
                        .divide(details.getShaShareOfPremiumPayable(), 3, RoundingMode.HALF_UP);

            }
            details.setNhaShareCorrespondingToShaRelease(re);
        }
        details.setTotalAmountPayableTillThisTranche(details.getNhaShareOfPremiumPayable().multiply(details.getPaymentTrancheNo()));
        BigDecimal min = minOfThree(
               details.getNhaShareOfPremiumPayable(),
                details.getNhaShareCorrespondingToShaRelease(),
                details.getTotalAmountPayableTillThisTranche()
        );
        details.setTotalAmountPayableByNhaAsOnDate(min);
        // final amount to be released
        BigDecimal finalAmount = min
                .subtract(details.getEarlierAmountReleasedByNha())
                .subtract(details.getUnspentAmountAsPerUc());

        details.setAmountProposedToBeReleased(finalAmount);
       return details;
    }
    public AashaHybrid AashaHybridCalc(AashaHybrid details)
    {





        // max GIA implementation/admin per family
        BigDecimal baseAdmin = BigDecimal.valueOf(50);
        BigDecimal population = details.getTotalEligibleAshaAwwAwhFamilies();

        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            baseAdmin = BigDecimal.valueOf(150);

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            baseAdmin = BigDecimal.valueOf(200);
        }
        // max GIA implementation/admin per family
        BigDecimal maxImplPerFamily = new BigDecimal("1052").multiply(details.getNhaShareInGia());

        BigDecimal maxAdminPerFamily = baseAdmin.multiply(details.getNhaShareInGia());
        details.setMaxGiaImplementationByNhaPerFamily(maxImplPerFamily);


        // max GIA by NHA
        BigDecimal maxImplByNha = details.getTotalEligibleAshaAwwAwhFamilies().multiply(maxImplPerFamily);
        BigDecimal maxAdminByNha = details.getTotalEligibleAshaAwwAwhFamilies().multiply(maxAdminPerFamily);
        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("20000000"));

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("10000000"));
        }
        else {
            maxAdminByNha = maxAdminByNha.max(new BigDecimal("150000000"));
        }

        details.setMaxGiaImplementationByNha(maxImplByNha);


        BigDecimal baseAmount = new BigDecimal("1052");
        if(details.getAnnualInsurancePremiumFamily().compareTo(BigDecimal.valueOf(1052)) > 0) {
            BigDecimal result = baseAmount
                    .multiply(details.getNhaShareInGia())
                    .multiply(details.getTotalEligibleAshaAwwAwhFamilies());

            details.setNhaShareOfPremiumPayable(result);
        }
        else {
            BigDecimal result=details.getAnnualInsurancePremiumFamily().multiply(details.getTotalEligibleAshaAwwAwhFamilies()).multiply(details.getNhaShareInGia());
            details.setNhaShareOfPremiumPayable(result);

        }
        BigDecimal shaShare = BigDecimal.ZERO;

        if (details.getAnnualInsurancePremiumFamily().compareTo(BigDecimal.valueOf(1052)) > 0) {
            shaShare = details.getAnnualInsurancePremiumFamily()
                    .subtract(details.getMaxGiaImplementationByNhaPerFamily())
                    .multiply(details.getTotalEligibleAshaAwwAwhFamilies());
        } else {
            BigDecimal nhaShareInGia = details.getNhaShareInGia();
            BigDecimal oneMinusNhaShare = BigDecimal.ONE.subtract(nhaShareInGia);

            shaShare = details.getAnnualInsurancePremiumFamily()
                    .multiply(oneMinusNhaShare)
                    .multiply(details.getTotalEligibleAshaAwwAwhFamilies());
        }

        details.setShaShareOfPremiumPayable(shaShare);


        BigDecimal re = details.getUpfrontReleaseByShaForPmjay()
                .multiply(details.getNhaShareInGia())
                .divide(BigDecimal.ONE.subtract(details.getNhaShareInGia()), 3, RoundingMode.HALF_UP);


        details.setNhaShareCorrespondingToShaRelease(re);

        details.setTotalAmountPayableTillThisTranche(details.getMaxGiaImplementationByNha().multiply(details.getPaymentTrancheNo()));
        BigDecimal min = minOfThree(
                details.getNhaShareOfPremiumPayable(),
                details.getNhaShareCorrespondingToShaRelease(),
                details.getTotalAmountPayableTillThisTranche()
        );
        details.setTotalAmountPayableByNhaAsOnDate(min);
        // final amount to be released
        BigDecimal finalAmount = min
                .subtract(details.getEarlierAmountReleasedByNha())
                .subtract(details.getUnspentAmountAsPerUc());

        details.setAmountProposedToBeReleased(finalAmount);
        return details;

        // max GIA implementation/admin per family

    }
    public AashaAdmin AashaAdminCalc(AashaAdmin details)
    {


        // max GIA implementation/admin per family
        BigDecimal baseAdmin = BigDecimal.valueOf(50);
        BigDecimal population = details.getTotalEligibleAshaAwwAwhFamilies();

        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            baseAdmin = BigDecimal.valueOf(150);

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            baseAdmin = BigDecimal.valueOf(200);
        }
        // max GIA implementation/admin per family
        BigDecimal maxImplPerFamily = new BigDecimal("1052").multiply(details.getNhaShareInGia());

        BigDecimal maxAdminPerFamily = baseAdmin.multiply(details.getNhaShareInGia());
        details.setMaxGiaImplementationByNhaPerFamily(maxImplPerFamily);
        details.setMaxGiaAdminByNhaPerFamily(maxAdminPerFamily);

        // max GIA by NHA
        BigDecimal maxImplByNha = details.getTotalEligibleAshaAwwAwhFamilies().multiply(maxImplPerFamily);
        BigDecimal maxAdminByNha = details.getTotalEligibleAshaAwwAwhFamilies().multiply(maxAdminPerFamily);
        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("20000000"));

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("10000000"));
        }
        else {
            maxAdminByNha = maxAdminByNha.max(new BigDecimal("150000000"));
        }

        details.setMaxGiaImplementationByNha(maxImplByNha);
        details.setMaxGiaAdminByNha(maxAdminByNha);


        BigDecimal treatmentCostForSHA = details.getTotalTreatmentCostPaidBySha()
                .multiply(BigDecimal.ONE.subtract(details.getNhaShareInGia()));

        details.setCostOfAdministrativeExpenseSha(treatmentCostForSHA);

        // NHA's share in PM-JAY treatment
        BigDecimal nhaShareInPmjay = details.getTotalTreatmentCostPaidBySha().multiply(details.getNhaShareInGia());
        details.setCostOfAdministrativeExpenseNha(nhaShareInPmjay);

        // NHA share corresponding to SHA release
        BigDecimal oneMinusShare = BigDecimal.ONE.subtract(details.getNhaShareInGia());
        BigDecimal nhaShareCorresponding ;
        if(details.getNhaShareInGia().equals(BigDecimal.ONE))
        {
            nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay().multiply(details.getNhaShareInGia());
        }
        else {
            nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay()
                    .multiply(details.getNhaShareInGia().divide(oneMinusShare, 10, RoundingMode.HALF_UP));
        }
        details.setNhaShareCorrespondingToShaRelease(nhaShareCorresponding);

        // total amount payable till this tranche
        BigDecimal totalTillTranche = maxAdminByNha.multiply(details.getPaymentTrancheNo());
        details.setTotalAmountPayableTillThisTranche(totalTillTranche);

        // Find minimum of 4 BigDecimal values
        BigDecimal min = minOfThree(
                maxAdminByNha,

                nhaShareCorresponding,
                totalTillTranche
        );
        details.setTotalAmountPayableByNhaAsOnDate(min);
        // final amount to be released
        BigDecimal finalAmount = min
                .subtract(details.getEarlierAmountReleasedByNha())
                .subtract(details.getUnspentAmountAsPerUc());

        details.setAmountProposedToBeReleased(finalAmount);

        return details;
    }
    public AdminNhaPaymentDetails administrativeCalc(AdminNhaPaymentDetails details)
    {
        if (details.getTotalPopulationCoveredAsPerMou() != null && details.getTotalPopulationCoveredAsPerMou().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal percentage = details.getEligibleSeccPopulation()
                    .divide(details.getTotalPopulationCoveredAsPerMou(), 10, RoundingMode.HALF_UP);
            details.setPercentageEligibleSeccPopulation(percentage);
        }

        // max GIA implementation/admin per family
        BigDecimal baseAdmin = BigDecimal.valueOf(50);
        BigDecimal population = details.getEligibleSeccPopulation();

        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            baseAdmin = BigDecimal.valueOf(150);

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            baseAdmin = BigDecimal.valueOf(200);
        }
        // max GIA implementation/admin per family
        BigDecimal maxImplPerFamily = new BigDecimal("1052").multiply(details.getNhaShareInGia());

        BigDecimal maxAdminPerFamily = baseAdmin.multiply(details.getNhaShareInGia());
        details.setMaxGiaImplementationPerFamily(maxImplPerFamily);
        details.setMaxGiaAdminPerFamily(maxAdminPerFamily);

        // max GIA by NHA
        BigDecimal maxImplByNha = details.getEligibleSeccPopulation().multiply(maxImplPerFamily);
        BigDecimal maxAdminByNha = details.getEligibleSeccPopulation().multiply(maxAdminPerFamily);
        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("20000000"));

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("10000000"));
        }
        else {
            maxAdminByNha = maxAdminByNha.max(new BigDecimal("150000000"));
        }

        details.setMaxGiaImplementationByNha(maxImplByNha);
        details.setMaxGiaAdminByNha(maxAdminByNha);


        BigDecimal treatmentCostForSHA = details.getTotalTreatmentCostPaidBySha()
                .multiply(BigDecimal.ONE.subtract(details.getNhaShareInGia()));

        details.setCostOfAdministrativeExpenseSha(treatmentCostForSHA);

        // NHA's share in PM-JAY treatment
        BigDecimal nhaShareInPmjay = details.getTotalTreatmentCostPaidBySha().multiply(details.getNhaShareInGia());
        details.setCostOfAdministrativeExpenseNha(nhaShareInPmjay);

        // NHA share corresponding to SHA release
        BigDecimal oneMinusShare = BigDecimal.ONE.subtract(details.getNhaShareInGia());
        BigDecimal nhaShareCorresponding ;
        if(details.getNhaShareInGia().equals(BigDecimal.ONE))
        {
            nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay().multiply(details.getNhaShareInGia());
        }
        else {
            nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay()
                    .multiply(details.getNhaShareInGia().divide(oneMinusShare, 10, RoundingMode.HALF_UP));
        }
        details.setNhaShareCorrespondingToShaRelease(nhaShareCorresponding);

        // total amount payable till this tranche
        BigDecimal totalTillTranche = maxAdminByNha.multiply(details.getPaymentTrancheNo());
        details.setTotalAmountPayableTillThisTranche(totalTillTranche);

        // Find minimum of 4 BigDecimal values
        BigDecimal min = minOfFour(
                maxAdminByNha,
details.getCostOfAdministrativeExpenseNha(),
                nhaShareCorresponding,
                totalTillTranche
        );
        details.setTotalAmountPayableByNhaAsOnDate(min);
        // final amount to be released
        BigDecimal finalAmount = min
                .subtract(details.getEarlierAmountReleasedByNha())
                .subtract(details.getUnspentAmountAsPerUc());

        details.setAmountProposedToBeReleased(finalAmount);

        return details;
    }
public VVSImplementationNewBenef vvsimplementationNewBenef(VVSImplementationNewBenef details)
{
    BigDecimal maxImplPerFamily = new BigDecimal("1052").multiply(details.getNhaShareInGia());

    BigDecimal maxPerFamily = maxImplPerFamily.multiply(details.getNewBeneficiaryInstate());
    BigDecimal maxPerFamilyOld=BigDecimal.valueOf(75.70).multiply(details.getNhaShareInGia());
    BigDecimal maxImpPerFamilyOld=maxPerFamilyOld.multiply(details.getOldBeneficiaryInState());

details.setMaxGiaImplementationPerFamilyOld(maxPerFamilyOld);

    details.setMaxGiaImplementationPerFamilyNew(maxImplPerFamily);
    details.setMaxGiaImplementationByNha(maxPerFamily.add(maxImpPerFamilyOld ));

    BigDecimal nhaShareInPmjay = details.getTotalTreatmentCostPaidBySha().multiply(details.getNhaShareInGia());
    details.setNhaShareInPmjayTreatmentCost(nhaShareInPmjay);

    BigDecimal oneMinusShare = BigDecimal.ONE.subtract(details.getNhaShareInGia());
    BigDecimal nhaShareCorresponding ;
    if(details.getNhaShareInGia().equals(BigDecimal.ONE))
    {
        nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay().multiply(details.getNhaShareInGia());
    }
    else {
        nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay()
                .multiply(details.getNhaShareInGia().divide(oneMinusShare, 10, RoundingMode.HALF_UP));
    }
    details.setNhaShareCorrespondingToShaRelease(nhaShareCorresponding);


    BigDecimal totalTillTranche = details.getMaxGiaImplementationByNha().multiply(details.getPaymentTrancheNo());
    details.setTotalAmountPayableTillThisTranche(totalTillTranche);

    BigDecimal min = minOfFour(
           details.getMaxGiaImplementationByNha(),
           details.getNhaShareInPmjayTreatmentCost(),
            nhaShareCorresponding,
            totalTillTranche
    );
    details.setTotalAmountPayableByNhaAsOnDate(min);

    BigDecimal finalAmount = min
            .subtract(details.getEarlierAmountReleasedByNha())
            .subtract(details.getUnspentAmountAsPerUc());

    details.setAmountProposedToBeReleased(finalAmount);

    return details;
}
public AashaImplementationTrust aashaImplementationTrust(AashaImplementationTrust details) {
    BigDecimal maxImplPerFamily = new BigDecimal("1052").multiply(details.getNhaShareInGia());

     details.setMaxGiaImplementationByNhaPerFamily(maxImplPerFamily);


    details.setMaxGiaImplementationByNha(details.getMaxGiaImplementationByNhaPerFamily().multiply(details.getTotalEligibleAshaAwwAwhFamilies()));


    // NHA's share in PM-JAY treatment
    BigDecimal nhaShareInPmjay = details.getTotalTreatmentCostPaidBySha().multiply(details.getNhaShareInGia());
    details.setNhaShareInCostForAshaAwsAww(nhaShareInPmjay);

    // NHA share corresponding to SHA release
    BigDecimal oneMinusShare = BigDecimal.ONE.subtract(details.getNhaShareInGia());
    BigDecimal nhaShareCorresponding ;
    if(details.getNhaShareInGia().equals(BigDecimal.ONE))
    {
        nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay().multiply(details.getNhaShareInGia());
    }
    else {
        nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay()
                .multiply(details.getNhaShareInGia().divide(oneMinusShare, 10, RoundingMode.HALF_UP));
    }
    details.setNhaShareCorrespondingToShaRelease(nhaShareCorresponding);

    // total amount payable till this tranche
    BigDecimal totalTillTranche = details.getMaxGiaImplementationByNha().multiply(details.getPaymentTrancheNo());
    details.setTotalAmountPayableTillThisTranche(totalTillTranche);

    // Find minimum of 4 BigDecimal values
    BigDecimal min = minOfFour(
            details.getMaxGiaImplementationByNha(),
            details.getNhaShareInCostForAshaAwsAww(),
            nhaShareCorresponding,
            totalTillTranche
    );
    details.setTotalAmountPayableByNhaAsOnDate(min);

    // final amount to be released
    BigDecimal finalAmount = min
            .subtract(details.getEarlierAmountReleasedByNha())
            .subtract(details.getUnspentAmountAsPerUc());

    details.setAmountProposedToBeReleased(finalAmount);

    return details;

}
    public VvsHybrid vvshybridcalc(VvsHybrid details)
    {
        BigDecimal maxImplPerFamily = new BigDecimal("1052").multiply(details.getNhaShareInGia());

        BigDecimal maxPerFamily = maxImplPerFamily.multiply(details.getNewBeneficiaryInstate());
        BigDecimal maxPerFamilyOld=BigDecimal.valueOf(75.70).multiply(details.getNhaShareInGia());
        BigDecimal maxImpPerFamilyOld=maxPerFamilyOld.multiply(details.getOldBeneficiaryInState());

        details.setMaxGiaImplementationPerFamilyOld(maxPerFamilyOld);

        details.setMaxGiaImplementationPerFamilyNew(maxImplPerFamily);
        details.setMaxGiaImplementationByNha(maxPerFamily.add(maxImpPerFamilyOld ));

        BigDecimal baseAmount = new BigDecimal("1052");
        if(details.getAnnualInsurancePremiumFamily().compareTo(BigDecimal.valueOf(1052)) > 0) {
            BigDecimal result = baseAmount
                    .multiply(details.getNhaShareInGia())
                    .multiply(details.getOldBeneficiaryInState());

            details.setNhaShareOfPremiumPayable(result);
        }
        else {
            BigDecimal result=details.getAnnualInsurancePremiumFamily().multiply(details.getOldBeneficiaryInState()).multiply(details.getNhaShareInGia());
            details.setNhaShareOfPremiumPayable(result);

        }
        BigDecimal shaShare = BigDecimal.ZERO;

        if (details.getAnnualInsurancePremiumFamily().compareTo(BigDecimal.valueOf(1052)) > 0) {
            shaShare = details.getAnnualInsurancePremiumFamily()
                    .subtract(maxImplPerFamily)
                    .multiply(details.getOldBeneficiaryInState());
        } else {
            BigDecimal nhaShareInGia = details.getNhaShareInGia();
            BigDecimal oneMinusNhaShare = BigDecimal.ONE.subtract(nhaShareInGia);

            shaShare = details.getAnnualInsurancePremiumFamily()
                    .multiply(oneMinusNhaShare)
                    .multiply(details.getOldBeneficiaryInState());
        }

        details.setShaShareOfPremiumPayable(shaShare);


        BigDecimal re;
        if(details.getNhaShareInGia().equals(BigDecimal.ONE))
        {
            re=details.getUpfrontReleaseByShaForPmjay().multiply(details.getNhaShareInGia());
        }
        else {
            re = details.getUpfrontReleaseByShaForPmjay()
                    .multiply(details.getNhaShareOfPremiumPayable())
                    .divide(details.getShaShareOfPremiumPayable(), 3, RoundingMode.HALF_UP);

        }

        details.setNhaShareCorrespondingToShaRelease(re);

        details.setTotalAmountPayableTillThisTranche(details.getMaxGiaImplementationByNha().multiply(details.getPaymentTrancheNo()));
        BigDecimal min = minOfThree(
                details.getNhaShareOfPremiumPayable(),
                details.getNhaShareCorrespondingToShaRelease(),
                details.getTotalAmountPayableTillThisTranche()
        );
        details.setTotalAmountPayableByNhaAsOnDate(min);
        // final amount to be released
        BigDecimal finalAmount = min
                .subtract(details.getEarlierAmountReleasedByNha())
                .subtract(details.getUnspentAmountAsPerUc());

        details.setAmountProposedToBeReleased(finalAmount);
        return details;



    }
    public VvsAdmin VvsAdmin(VvsAdmin details
    )
    {
        BigDecimal maxImplPerFamily = new BigDecimal("1052").multiply(details.getNhaShareInGia());

        BigDecimal maxPerFamily = maxImplPerFamily.multiply(details.getNewBeneficiaryInstate());
        BigDecimal maxPerFamilyOld=BigDecimal.valueOf(75.70).multiply(details.getNhaShareInGia());
        BigDecimal maxImpPerFamilyOld=maxPerFamilyOld.multiply(details.getOldBeneficiaryInState());

        details.setMaxGiaImplementationPerFamilyOld(maxPerFamilyOld);

        details.setMaxGiaImplementationPerFamilyNew(maxImplPerFamily);
        details.setMaxGiaImplementationByNha(maxPerFamily.add(maxImpPerFamilyOld ));
        BigDecimal baseAdmin = BigDecimal.valueOf(50);
        BigDecimal population = details.getNewBeneficiaryInstate();

        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            baseAdmin = BigDecimal.valueOf(150);

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            baseAdmin = BigDecimal.valueOf(200);
        }
        BigDecimal maxAdminPerFamily = baseAdmin.multiply(details.getNhaShareInGia());
        BigDecimal maxAdminByNha = details.getNewBeneficiaryInstate().multiply(maxAdminPerFamily);
        if (population.compareTo(BigDecimal.valueOf(100000)) > 0 &&
                population.compareTo(BigDecimal.valueOf(1000000)) < 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("20000000"));

        } else if (population.compareTo(BigDecimal.valueOf(100000)) <= 0) {

            maxAdminByNha = maxAdminByNha.max(new BigDecimal("10000000"));
        }
        else {
            maxAdminByNha = maxAdminByNha.max(new BigDecimal("150000000"));
        }

      details.setMaxGiaAdminByNha(maxAdminByNha);

        BigDecimal treatmentCostForSHA = details.getTotalTreatmentCostPaidBySha()
                .multiply(BigDecimal.ONE.subtract(details.getNhaShareInGia()));

        details.setCostOfAdministrativeExpenseSha(treatmentCostForSHA);

        // NHA's share in PM-JAY treatment
        BigDecimal nhaShareInPmjay = details.getTotalTreatmentCostPaidBySha().multiply(details.getNhaShareInGia());
        details.setCostOfAdministrativeExpenseNha(nhaShareInPmjay);

        // NHA share corresponding to SHA release
        BigDecimal oneMinusShare = BigDecimal.ONE.subtract(details.getNhaShareInGia());
        BigDecimal nhaShareCorresponding ;
        if(details.getNhaShareInGia().equals(BigDecimal.ONE))
        {
            nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay().multiply(details.getNhaShareInGia());
        }
        else {
            nhaShareCorresponding = details.getUpfrontReleaseByShaForPmjay()
                    .multiply(details.getNhaShareInGia().divide(oneMinusShare, 10, RoundingMode.HALF_UP));
        }
        details.setNhaShareCorrespondingToShaRelease(nhaShareCorresponding);

        // total amount payable till this tranche
        BigDecimal totalTillTranche = maxAdminByNha.multiply(details.getPaymentTrancheNo());
        details.setTotalAmountPayableTillThisTranche(totalTillTranche);

        // Find minimum of 4 BigDecimal values
        BigDecimal min = minOfThree(
                maxAdminByNha,

                nhaShareCorresponding,
                totalTillTranche
        );
        details.setTotalAmountPayableByNhaAsOnDate(min);
        // final amount to be released
        BigDecimal finalAmount = min
                .subtract(details.getEarlierAmountReleasedByNha())
                .subtract(details.getUnspentAmountAsPerUc());

        details.setAmountProposedToBeReleased(finalAmount);

        return details;


    }
    private BigDecimal minOfFour(BigDecimal a, BigDecimal b, BigDecimal c, BigDecimal d) {
        return a.min(b).min(c).min(d);
    }
    private BigDecimal minOfThree(BigDecimal a, BigDecimal b, BigDecimal c) {
        return a.min(b).min(c);
    }
}
