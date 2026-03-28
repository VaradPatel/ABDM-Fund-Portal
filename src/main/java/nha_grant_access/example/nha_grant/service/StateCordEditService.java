package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.AashaAdmin;
import nha_grant_access.example.nha_grant.dto.AashaHybrid;
import nha_grant_access.example.nha_grant.dto.VvsAdmin;
import nha_grant_access.example.nha_grant.dto.VvsHybrid;
import nha_grant_access.example.nha_grant.entity.*;
import nha_grant_access.example.nha_grant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StateCordEditService {
    @Autowired
    private IGrantRequestsRepo iGrantRequestsRepo;
    @Autowired
    IactionRepository iactionRepository;
    @Autowired
    private IstatesRepository statesRepository;
    @Autowired
    private VvsAdminRepo vvsAdminRepo;
    @Autowired
    private IWorkFlow iWorkFlow;
    @Autowired
    private IVvsHybrid iVvsHybrid;
    @Autowired
    private IRolesRepository roles;
    @Autowired
    private IAashaAdmin iAashaAdmin;
    @Autowired
    private IDashboardRepo iDashboardRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ImplementationTypesRepo implementationTypes;
    @Autowired
    private IProposalTypesRepo iProposalTypesRepo;
    @Autowired
    private IStatusDescription iStatusDescription;
    @Autowired
    private IWorkFlowConfRepo iWorkFlowConfRepo;
    @Autowired
    private ImplementInsuCalcRepo implementInsuCalcRepo;
    @Autowired
    private GrantRequestService grantRequestService;
    @Autowired
    private ImplementTrustCalcRepo implementationTypesRepo;
    @Autowired
    private IAdminCalcRepo iAdminCalcRepo;
    @Autowired
    private IvvsImpleNew ivvsImpleNew;
    @Autowired
    private IashaImplTrust iashaImplTrust;
    @Autowired
    private IAashaHybrid aashaHybridRepo;
    public void saveToCalcFlow(  stateCordEdit dto1, GrantRequests savedRequest)
    {
        if(dto1.getImplementationTrustNhaPayementDetails()!=null)
        {
            ImplementationTrustNhaPayementDetails dto =dto1.getImplementationTrustNhaPayementDetails();
            ImplementTrustCalc implementTrustCalc= ImplementTrustCalc.builder().
                    stateName(dto.getStateName())
                    .modeOfImplementation(dto.getModeOfImplementation())
                    .dateOfImplementation(dto.getDateOfImplementation())
                    .benefitCover(dto.getBenefitCover())
                    .nhaShareInGia(dto.getNhaShareInGia())
                    .schemeName(dto.getSchemeName())
                    .totalPopulationCoveredAsPerMou(dto.getTotalPopulationCoveredAsPerMou())
                    .eligibleSeccPopulation(dto.getEligibleSeccPopulation())
                    .percentageEligibleSeccPopulation(dto.getPercentageEligibleSeccPopulation())
                    .maxGiaImplementationPerFamily(dto.getMaxGiaImplementationPerFamily())
                    .maxGiaAdminPerFamily(dto.getMaxGiaAdminPerFamily())
                    .maxGiaImplementationByNha(dto.getMaxGiaImplementationByNha())
                    .maxGiaAdminByNha(dto.getMaxGiaAdminByNha())
                    .policyPeriod(dto.getPolicyPeriod())
                    .totalTreatmentCostPaidBySha(dto.getTotalTreatmentCostPaidBySha())
                    .treatmentCostForPmjayBeneficiaries(dto.getTreatmentCostForPmjayBeneficiaries())
                    .nhaShareInPmjayTreatmentCost(dto.getNhaShareInPmjayTreatmentCost())
                    .upfrontReleaseByShaForPmjay(dto.getUpfrontReleaseByShaForPmjay())
                    .nhaShareCorrespondingToShaRelease(dto.getNhaShareCorrespondingToShaRelease())
                    .paymentTrancheNo(dto.getPaymentTrancheNo())
                    .totalAmountPayableTillThisTranche(dto.getTotalAmountPayableTillThisTranche())
                    .totalAmountPayableByNhaAsOnDate(dto.getTotalAmountPayableByNhaAsOnDate())
                    .earlierAmountReleasedByNha(dto.getEarlierAmountReleasedByNha())
                    .unspentAmountAsPerUc(dto.getUnspentAmountAsPerUc())
                    .amountProposedToBeReleased(dto.getAmountProposedToBeReleased())
                    .requestId(savedRequest.getRequestId())
                    .roleId(3)
                    .userId(savedRequest.getUser().getId()).build();
            implementationTypesRepo.save(implementTrustCalc);

        }
        else if(dto1.getAdminNhaPaymentDetails()!=null)
        {
            AdminNhaPaymentDetails dto =dto1.getAdminNhaPaymentDetails();
            AdministrativeCalc administrativeCalc= AdministrativeCalc.builder().
                    stateName(dto.getStateName())
                    .modeOfImplementation(dto.getModeOfImplementation())
                    .dateOfImplementation(dto.getDateOfImplementation())
                    .benefitCover(dto.getBenefitCover())
                    .nhaShareInGia(dto.getNhaShareInGia())
                    .schemeName(dto.getSchemeName())
                    .totalPopulationCoveredAsPerMou(dto.getTotalPopulationCoveredAsPerMou())
                    .eligibleSeccPopulation(dto.getEligibleSeccPopulation())
                    .percentageEligibleSeccPopulation(dto.getPercentageEligibleSeccPopulation())
                    .maxGiaImplementationPerFamily(dto.getMaxGiaImplementationPerFamily())
                    .maxGiaAdminPerFamily(dto.getMaxGiaAdminPerFamily())
                    .maxGiaImplementationByNha(dto.getMaxGiaImplementationByNha())
                    .maxGiaAdminByNha(dto.getMaxGiaAdminByNha())
                    .policyPeriod(dto.getPolicyPeriod())
                    .totalTreatmentCostPaidBySha(dto.getTotalTreatmentCostPaidBySha())
                    .costOfAdministrativeExpenseNha(dto.getCostOfAdministrativeExpenseNha())
                    .costOfAdministrativeExpenseSha(dto.getCostOfAdministrativeExpenseSha())

                    .upfrontReleaseByShaForPmjay(dto.getUpfrontReleaseByShaForPmjay())
                    .nhaShareCorrespondingToShaRelease(dto.getNhaShareCorrespondingToShaRelease())
                    .paymentTrancheNo(dto.getPaymentTrancheNo())
                    .totalAmountPayableTillThisTranche(dto.getTotalAmountPayableTillThisTranche())
                    .totalAmountPayableByNhaAsOnDate(dto.getTotalAmountPayableByNhaAsOnDate())
                    .earlierAmountReleasedByNha(dto.getEarlierAmountReleasedByNha())
                    .unspentAmountAsPerUc(dto.getUnspentAmountAsPerUc())
                    .amountProposedToBeReleased(dto.getAmountProposedToBeReleased())
                    .requestId(savedRequest.getRequestId())
                    .roleId(3)
                    .userId(savedRequest.getUser().getId()).build();
            iAdminCalcRepo.save(administrativeCalc);


        }
        else if(dto1.getImplementationInsurancePayementDetails()!=null)
        {
            ImplementationInsurancePayementDetails dto =dto1.getImplementationInsurancePayementDetails();
            ImplementInsuCalc implementInsuCalc= ImplementInsuCalc.builder().
                    stateName(dto.getStateName())
                    .modeOfImplementation(dto.getModeOfImplementation())
                    .dateOfImplementation(dto.getDateOfImplementation())
                    .benefitCover(dto.getBenefitCover())
                    .nhaShareInGia(dto.getNhaShareInGia())
                    .schemeName(dto.getSchemeName())
                    .totalPopulationCoveredAsPerMou(dto.getTotalPopulationCoveredAsPerMou())
                    .eligibleSeccPopulation(dto.getEligibleSeccPopulation())
                    .percentageEligibleSeccPopulation(dto.getPercentageEligibleSeccPopulation())
                    .maxGiaImplementationPerFamily(dto.getMaxGiaImplementationPerFamily())
                    .maxGiaAdminPerFamily(dto.getMaxGiaAdminPerFamily())
                    .maxGiaImplementationByNha(dto.getMaxGiaImplementationByNha())
                    .maxGiaAdminByNha(dto.getMaxGiaAdminByNha())
                    .policyPeriod(dto.getPolicyPeriod())

                    .nameOfInsuranceCompany(dto.getNameOfInsuranceCompany())
                    .annualInsurancePremiumFamily(dto.getAnnualInsurancePremiumFamily())
                    .shaShareOfPremiumPayable(dto.getShaShareOfPremiumPayable())
                    .nhaShareOfPremiumPayable(dto.getNhaShareOfPremiumPayable())

                    .upfrontReleaseByShaForPmjay(dto.getUpfrontReleaseByShaForPmjay())
                    .nhaShareCorrespondingToShaRelease(dto.getNhaShareCorrespondingToShaRelease())
                    .paymentTrancheNo(dto.getPaymentTrancheNo())
                    .totalAmountPayableTillThisTranche(dto.getTotalAmountPayableTillThisTranche())
                    .totalAmountPayableByNhaAsOnDate(dto.getTotalAmountPayableByNhaAsOnDate())
                    .earlierAmountReleasedByNha(dto.getEarlierAmountReleasedByNha())
                    .unspentAmountAsPerUc(dto.getUnspentAmountAsPerUc())
                    .amountProposedToBeReleased(dto.getAmountProposedToBeReleased())
                    .requestId(savedRequest.getRequestId())
                    .roleId(3)
                    .userId(savedRequest.getUser().getId()).build();
            implementInsuCalcRepo.save(implementInsuCalc);


        }
        else if(dto1.getVvsImplementationNewBenef()!=null)
        {
            VVSImplementationNewBenef vvsImplementationNewBenef=dto1.getVvsImplementationNewBenef();
            VVSImplementNew vvsImplementNew = VVSImplementNew.builder()
                    .requestId(savedRequest.getRequestId())


                    .stateName(vvsImplementationNewBenef.getStateName())
                    .modeOfImplementation(vvsImplementationNewBenef.getModeOfImplementation())
                    .dateOfImplementation(vvsImplementationNewBenef.getDateOfImplementation())
                    .benefitCover(vvsImplementationNewBenef.getBenefitCover())
                    .nhaShareInGia(vvsImplementationNewBenef.getNhaShareInGia())
                    .schemeName(vvsImplementationNewBenef.getSchemeName())
                    .newBeneficiaryInstate(vvsImplementationNewBenef.getNewBeneficiaryInstate())
                    .oldBeneficiaryInState(vvsImplementationNewBenef.getOldBeneficiaryInState())
                    .maxGiaImplementationPerFamilyNew(vvsImplementationNewBenef.getMaxGiaImplementationPerFamilyNew())
                    .maxGiaImplementationPerFamilyOld(vvsImplementationNewBenef.getMaxGiaImplementationPerFamilyOld())
                    .maxGiaImplementationByNha(vvsImplementationNewBenef.getMaxGiaImplementationByNha())
                    .policyPeriod(vvsImplementationNewBenef.getPolicyPeriod())
                    .totalTreatmentCostPaidBySha(vvsImplementationNewBenef.getTotalTreatmentCostPaidBySha())
                    .nhaShareInPmjayTreatmentCost(vvsImplementationNewBenef.getNhaShareInPmjayTreatmentCost())
                    .upfrontReleaseByShaForPmjay(vvsImplementationNewBenef.getUpfrontReleaseByShaForPmjay())
                    .nhaShareCorrespondingToShaRelease(vvsImplementationNewBenef.getNhaShareCorrespondingToShaRelease())
                    .paymentTrancheNo(vvsImplementationNewBenef.getPaymentTrancheNo())
                    .totalAmountPayableTillThisTranche(vvsImplementationNewBenef.getTotalAmountPayableTillThisTranche())
                    .totalAmountPayableByNhaAsOnDate(vvsImplementationNewBenef.getTotalAmountPayableByNhaAsOnDate())
                    .earlierAmountReleasedByNha(vvsImplementationNewBenef.getEarlierAmountReleasedByNha())
                    .unspentAmountAsPerUc(vvsImplementationNewBenef.getUnspentAmountAsPerUc())
                    .amountProposedToBeReleased(vvsImplementationNewBenef.getAmountProposedToBeReleased())
                    .roleId(3)
                    .build();
            ivvsImpleNew.save(vvsImplementNew);

        }
        else if(dto1.getAashaImplementationTrust()!=null)
        {
            AashaImplementationTrust details=dto1.getAashaImplementationTrust();
            AashaImplTrust entity = AashaImplTrust.builder()
                    .stateName(details.getStateName())
                    .modeOfImplementation(details.getModeOfImplementation())
                    .dateOfImplementation(details.getDateOfImplementation())
                    .benefitCover(details.getBenefitCover())
                    .nhaShareInGia(details.getNhaShareInGia())
                    .schemeName(details.getSchemeName())
                    .totalFamiliesCoveredInStateAsPerMou(details.getTotalFamiliesCoveredInStateAsPerMou())
                    .totalEligibleAshaAwwAwhFamilies(details.getTotalEligibleAshaAwwAwhFamilies())
                    .maxGiaImplementationByNhaPerFamily(details.getMaxGiaImplementationByNhaPerFamily())
                    .maxGiaImplementationByNha(details.getMaxGiaImplementationByNha())
                    .policyPeriod(details.getPolicyPeriod())

                    .totalTreatmentCostPaidBySha(details.getTotalTreatmentCostPaidBySha())
                    .nhaShareInCostForAshaAwsAww(details.getNhaShareInCostForAshaAwsAww())
                    .upfrontReleaseByShaForPmjay(details.getUpfrontReleaseByShaForPmjay())
                    .nhaShareCorrespondingToShaRelease(details.getNhaShareCorrespondingToShaRelease())
                    .paymentTrancheNo(details.getPaymentTrancheNo())
                    .totalAmountPayableTillThisTranche(details.getTotalAmountPayableTillThisTranche())
                    .totalAmountPayableByNhaAsOnDate(details.getTotalAmountPayableByNhaAsOnDate())
                    .earlierAmountReleasedByNha(details.getEarlierAmountReleasedByNha())
                    .unspentAmountAsPerUc(details.getUnspentAmountAsPerUc())
                    .amountProposedToBeReleased(details.getAmountProposedToBeReleased())

                    .roleId(3)
                    .requestId(savedRequest.getRequestId())
                    .build();

            iashaImplTrust.save(entity);
        }
        else if (dto1.getAashaHybrid() != null) {
            AashaHybrid dto = dto1.getAashaHybrid();

            nha_grant_access.example.nha_grant.entity.AashaHybrid aashaHybridEntity = nha_grant_access.example.nha_grant.entity.AashaHybrid.builder()
                    .stateName(dto.getStateName())
                    .modeOfImplementation(dto.getModeOfImplementation())
                    .dateOfImplementation(dto.getDateOfImplementation())
                    .benefitCover(dto.getBenefitCover())
                    .nhaShareInGia(dto.getNhaShareInGia())
                    .schemeName(dto.getSchemeName())
                    .totalFamiliesCoveredInStateAsPerMou(dto.getTotalFamiliesCoveredInStateAsPerMou())
                    .totalEligibleAshaAwwAwhFamilies(dto.getTotalEligibleAshaAwwAwhFamilies())
                    .maxGiaImplementationByNhaPerFamily(dto.getMaxGiaImplementationByNhaPerFamily())
                    .maxGiaImplementationByNha(dto.getMaxGiaImplementationByNha())
                    .policyPeriod(dto.getPolicyPeriod())
                    .nameOfInsuranceCompany(dto.getNameOfInsuranceCompany())
                    .annualInsurancePremiumFamily(dto.getAnnualInsurancePremiumFamily())
                    .nhaShareOfPremiumPayable(dto.getNhaShareOfPremiumPayable())
                    .shaShareOfPremiumPayable(dto.getShaShareOfPremiumPayable())
                    .upfrontReleaseByShaForPmjay(dto.getUpfrontReleaseByShaForPmjay())
                    .nhaShareCorrespondingToShaRelease(dto.getNhaShareCorrespondingToShaRelease())
                    .paymentTrancheNo(dto.getPaymentTrancheNo())
                    .totalAmountPayableTillThisTranche(dto.getTotalAmountPayableTillThisTranche())
                    .totalAmountPayableByNhaAsOnDate(dto.getTotalAmountPayableByNhaAsOnDate())
                    .earlierAmountReleasedByNha(dto.getEarlierAmountReleasedByNha())
                    .unspentAmountAsPerUc(dto.getUnspentAmountAsPerUc())
                    .amountProposedToBeReleased(dto.getAmountProposedToBeReleased())
                    .requestId(savedRequest.getRequestId())
                    .roleId(3)
                    .build();

            aashaHybridRepo.save(aashaHybridEntity);
        }
        else if (dto1.getAashaAdmin() != null) {
            AashaAdmin dto = dto1.getAashaAdmin();

            nha_grant_access.example.nha_grant.entity.AashaAdmin entity = nha_grant_access.example.nha_grant.entity.AashaAdmin.builder()
                    .stateName(dto.getStateName())
                    .modeOfImplementation(dto.getModeOfImplementation())
                    .dateOfImplementation(dto.getDateOfImplementation())
                    .benefitCover(dto.getBenefitCover())
                    .nhaShareInGia(dto.getNhaShareInGia())
                    .schemeName(dto.getSchemeName())
                    .totalFamiliesCoveredInStateAsPerMou(dto.getTotalFamiliesCoveredInStateAsPerMou())
                    .totalEligibleAshaAwwAwhFamilies(dto.getTotalEligibleAshaAwwAwhFamilies())
                    .maxGiaImplementationByNhaPerFamily(dto.getMaxGiaImplementationByNhaPerFamily())
                    .maxGiaImplementationByNha(dto.getMaxGiaImplementationByNha())
                    .maxGiaAdminByNhaPerFamily(dto.getMaxGiaAdminByNhaPerFamily())
                    .maxGiaAdminByNha(dto.getMaxGiaAdminByNha())
                    .policyPeriod(dto.getPolicyPeriod())
                    .totalTreatmentCostPaidBySha(dto.getTotalTreatmentCostPaidBySha())
                    .costOfAdministrativeExpenseSha(dto.getCostOfAdministrativeExpenseSha())
                    .costOfAdministrativeExpenseNha(dto.getCostOfAdministrativeExpenseNha())
                    .upfrontReleaseByShaForPmjay(dto.getUpfrontReleaseByShaForPmjay())
                    .nhaShareCorrespondingToShaRelease(dto.getNhaShareCorrespondingToShaRelease())
                    .paymentTrancheNo(dto.getPaymentTrancheNo())
                    .totalAmountPayableTillThisTranche(dto.getTotalAmountPayableTillThisTranche())
                    .totalAmountPayableByNhaAsOnDate(dto.getTotalAmountPayableByNhaAsOnDate())
                    .earlierAmountReleasedByNha(dto.getEarlierAmountReleasedByNha())
                    .unspentAmountAsPerUc(dto.getUnspentAmountAsPerUc())
                    .amountProposedToBeReleased(dto.getAmountProposedToBeReleased())
                    .requestId(savedRequest.getRequestId())
                    .roleId(3)
                    .build();

            iAashaAdmin.save(entity);
        }
        System.out.println("sdsds");
        if (dto1.getVvsHybrid() != null) {
            VvsHybrid dto = dto1.getVvsHybrid();

            nha_grant_access.example.nha_grant.entity.VvsHybrid vvsHybrid = nha_grant_access.example.nha_grant.entity.VvsHybrid.builder()
                    .stateName(dto.getStateName())
                    .modeOfImplementation(dto.getModeOfImplementation())
                    .dateOfImplementation(dto.getDateOfImplementation())
                    .benefitCover(dto.getBenefitCover())
                    .nhaShareInGia(dto.getNhaShareInGia())
                    .schemeName(dto.getSchemeName())
                    .newBeneficiaryInstate(dto.getNewBeneficiaryInstate())
                    .oldBeneficiaryInState(dto.getOldBeneficiaryInState())
                    .maxGiaImplementationPerFamilyNew(dto.getMaxGiaImplementationPerFamilyNew())
                    .maxGiaImplementationPerFamilyOld(dto.getMaxGiaImplementationPerFamilyOld())
                    .maxGiaImplementationByNha(dto.getMaxGiaImplementationByNha())
                    .nameOfInsuranceCompany(dto.getNameOfInsuranceCompany())
                    .annualInsurancePremiumFamily(dto.getAnnualInsurancePremiumFamily())
                    .nhaShareOfPremiumPayable(dto.getNhaShareOfPremiumPayable())
                    .shaShareOfPremiumPayable(dto.getShaShareOfPremiumPayable())
                    .upfrontReleaseByShaForPmjay(dto.getUpfrontReleaseByShaForPmjay())
                    .nhaShareCorrespondingToShaRelease(dto.getNhaShareCorrespondingToShaRelease())
                    .paymentTrancheNo(dto.getPaymentTrancheNo())
                    .totalAmountPayableTillThisTranche(dto.getTotalAmountPayableTillThisTranche())
                    .totalAmountPayableByNhaAsOnDate(dto.getTotalAmountPayableByNhaAsOnDate())
                    .earlierAmountReleasedByNha(dto.getEarlierAmountReleasedByNha())
                    .unspentAmountAsPerUc(dto.getUnspentAmountAsPerUc())
                    .amountProposedToBeReleased(dto.getAmountProposedToBeReleased())
                    .requestId(savedRequest.getRequestId())
                    .roleId(3)
                    .build();

            iVvsHybrid.save(vvsHybrid);
        }
        if (dto1.getVvsAdmin() != null) {
            VvsAdmin dto = dto1.getVvsAdmin();

            nha_grant_access.example.nha_grant.entity.VvsAdmin vvsAdmin = nha_grant_access.example.nha_grant.entity.VvsAdmin.builder()
                    .stateName(dto.getStateName())
                    .modeOfImplementation(dto.getModeOfImplementation())
                    .dateOfImplementation(dto.getDateOfImplementation())
                    .benefitCover(dto.getBenefitCover())
                    .nhaShareInGia(dto.getNhaShareInGia())
                    .schemeName(dto.getSchemeName())
                    .newBeneficiaryInstate(dto.getNewBeneficiaryInstate())
                    .oldBeneficiaryInState(dto.getOldBeneficiaryInState())
                    .maxGiaImplementationPerFamilyNew(dto.getMaxGiaImplementationPerFamilyNew())
                    .maxGiaImplementationPerFamilyOld(dto.getMaxGiaImplementationPerFamilyOld())
                    .maxGiaImplementationByNha(dto.getMaxGiaImplementationByNha())
                    .maxGiaAdminByNha(dto.getMaxGiaAdminByNha())
                    .policyPeriod(dto.getPolicyPeriod())
                    .totalTreatmentCostPaidBySha(dto.getTotalTreatmentCostPaidBySha())
                    .costOfAdministrativeExpenseSha(dto.getCostOfAdministrativeExpenseSha())
                    .costOfAdministrativeExpenseNha(dto.getCostOfAdministrativeExpenseNha())
                    .upfrontReleaseByShaForPmjay(dto.getUpfrontReleaseByShaForPmjay())
                    .nhaShareCorrespondingToShaRelease(dto.getNhaShareCorrespondingToShaRelease())
                    .paymentTrancheNo(dto.getPaymentTrancheNo())
                    .totalAmountPayableTillThisTranche(dto.getTotalAmountPayableTillThisTranche())
                    .totalAmountPayableByNhaAsOnDate(dto.getTotalAmountPayableByNhaAsOnDate())
                    .earlierAmountReleasedByNha(dto.getEarlierAmountReleasedByNha())
                    .unspentAmountAsPerUc(dto.getUnspentAmountAsPerUc())
                    .amountProposedToBeReleased(dto.getAmountProposedToBeReleased())
                    .requestId(savedRequest.getRequestId())
                    .roleId(3)
                    .build();
            vvsAdminRepo.save(vvsAdmin);
        }


    }
}
