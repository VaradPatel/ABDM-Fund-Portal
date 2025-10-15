package nha_grant_access.example.nha_grant.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.AashaAdmin;
import nha_grant_access.example.nha_grant.dto.AashaHybrid;
import nha_grant_access.example.nha_grant.dto.VvsAdmin;
import nha_grant_access.example.nha_grant.dto.VvsHybrid;
import nha_grant_access.example.nha_grant.entity.*;
import nha_grant_access.example.nha_grant.entity.Roles;
import nha_grant_access.example.nha_grant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hibernate.boot.model.process.spi.MetadataBuildingProcess.build;

@Service
@Slf4j
public class GrantRequestService implements IGrantRequests {

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


    @Override
    @Transactional
    public GrantRequestInputDto saveGrantRequest(GrantRequestInputDto dto1, Boolean isQueryResponse) {
        try {
            if (dto1.getRequestId() != null) {
                // Update Existing Request
                GrantRequests existingRequest = iGrantRequestsRepo.findGrantRequestByRequestId(dto1.getRequestId());
                if (existingRequest == null) {
                    throw new RuntimeException("Invalid RequestID");
                }
               existingRequest=updateExistingGrantRequest(existingRequest, dto1);
                iGrantRequestsRepo.save(existingRequest);
                if(isQueryResponse)
                {
                    saveToWorkFlow(existingRequest.getRequestId(),existingRequest.getUser().getId(),7,existingRequest.getProposalType().getId(),dto1.getQueryResponse());

                }
                saveToCalcFlow(dto1,existingRequest);


                log.info("edited the grant_requests " + existingRequest.toString());

            }
            else {
                // Create New Request
                GrantRequests newRequest = mapToEntity(dto1);
                GrantRequests savedRequest = iGrantRequestsRepo.save(newRequest);
                saveToDashboard(savedRequest.getRequestId(),savedRequest.getState().getId(),savedRequest.getProposalType().getId(),1,2,4,null,null,2);
                dto1.setRequestId(savedRequest.getRequestId());
                log.info("saved the grant_requests " + savedRequest.toString());
                //save to user dump
                saveToWorkFlow(savedRequest.getRequestId(),savedRequest.getUser().getId(),1,savedRequest.getProposalType().getId(),"");
                saveToCalcFlow(dto1,savedRequest);

            }
            return dto1;
        } catch (Exception e) {
            throw new RuntimeException("Not able to process your request", e);
        }
    }

    @Override
    public List<AllGrantRequest> getAllGrantRequestByState(List<Integer> stateId, Integer userId) {


        return iGrantRequestsRepo.findAllGrantRequest(stateId,userId).stream()
                .map(gr -> AllGrantRequest.builder()
                        .requestId(gr.getRequestId())
                        .dateRequested(gr.getCreatedAt())
                        .releaseAmount(gr.getReleasedAmount())
                        .requestedAmount(gr.getRequestedAmount())
                        .requestStatus(Optional.ofNullable(gr.getStatusDescription())
                                .map(StatusDescription::getDescription)
                                .orElse(""))
                        .proposalType(gr.getProposalType())
                        .implementationTypes(gr.getImplementationMode())
                        .Tranche(gr.getTranche())
                        .policyEndDate(gr.getPolicyEndDate())
                        .policyStartDate(gr.getPolicyStartDate())
                        .financialYear(gr.getFinancialYear())
                        .sanctionDate(gr.getSanctionDate())
                        .states(gr.getState())
                        .statusId(gr.getStatusDescription().getId())
                        .sanction_letter(gr.getSanction_letter())
                        .amountGc(gr.getAmountGc()).amountSt(gr.getAmountSt()).amountSc(gr.getAmountSc())
                        .schemeId(gr.getSchemeId())
                        .schemeName(gr.getSchemeName())





                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<AllGrantRequest> getAllGrantRequest() {
        return iGrantRequestsRepo.findAllGrantRequestForReviwer().stream()
                .map(gr -> AllGrantRequest.builder()
                        .requestId(gr.getRequestId())
                        .dateRequested(gr.getCreatedAt())
                        .releaseAmount(gr.getReleasedAmount())
                        .requestedAmount(gr.getRequestedAmount())
                        .requestStatus(Optional.ofNullable(gr.getStatusDescription())
                                .map(StatusDescription::getDescription)
                                .orElse(""))
                        .proposalType(gr.getProposalType())
                        .implementationTypes(gr.getImplementationMode())
                        .Tranche(gr.getTranche())
                        .policyEndDate(gr.getPolicyEndDate())
                        .policyStartDate(gr.getPolicyStartDate())
                        .financialYear(gr.getFinancialYear())
                        .sanctionDate(gr.getSanctionDate())
                        .states(gr.getState())
                        .sanction_letter(gr.getSanction_letter())
                        .amountGc(gr.getAmountGc()).amountSc(gr.getAmountSc()).amountSt(gr.getAmountSt())
                        .schemeId(gr.getSchemeId())
                        .schemeName(gr.getSchemeName())

                        .build())
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public String stateCeoApprove(RequestId requestId) {
        try {
            WorkFlowConfiguration workFlowConfiguration = iWorkFlowConfRepo.findByActionPerformedIdAndActionPerformedById(3, 2);
            iGrantRequestsRepo.updateStatusDescription(requestId.getRequestId(), workFlowConfiguration.getStatusDescription().getId());
            grantRequestService.saveToWorkFlow(requestId.getRequestId(), requestId.getUserId(), 3, requestId.getProposalTypeId(), "");
        }
        catch (Exception e)
        {
            log.error(e.toString());
        }
        return null;
    }

    @Override
    @Transactional
    public String stateCordApprove(RequestId requestId) {
        try {
            WorkFlowConfiguration workFlowConfiguration = iWorkFlowConfRepo.findByActionPerformedIdAndActionPerformedById(3, 3);
            iGrantRequestsRepo.updateStatusDescription(requestId.getRequestId(), workFlowConfiguration.getStatusDescription().getId());
            grantRequestService.saveToWorkFlow(requestId.getRequestId(), requestId.getUserId(), 3, requestId.getProposalTypeId(), "");
        }
        catch (Exception e)
        {
            log.error(e.toString());
        }
        return null;
    }

    @Override
    @Transactional
    public String nhaReviewerApprove(RequestId requestId) {
        try {
            WorkFlowConfiguration workFlowConfiguration = iWorkFlowConfRepo.findByActionPerformedIdAndActionPerformedById(3, 4);
            iGrantRequestsRepo.updateStatusDescription(requestId.getRequestId(), workFlowConfiguration.getStatusDescription().getId());
            grantRequestService.saveToWorkFlow(requestId.getRequestId(), requestId.getUserId(), 3, requestId.getProposalTypeId(), "");
        }
        catch (Exception e)
        {
            log.error(e.toString());
        }
        return null;
    }

    private GrantRequests mapToEntity(GrantRequestInputDto dto) {
        GrantRequests grantRequests=GrantRequests.builder()
                .requestId(generateUniqueRequestId())
                .state(getState(dto.getStateId()))
                .user(getUser(dto.getUserId()))
                .proposalType(getProposalType(dto.getProposalTypeId()))
                .implementationMode(getImplementationType(dto.getImplementationModeId()))
                .insuranceCompany(dto.getInsuranceCompany())
                .policyStartDate(dto.getPolicyStartDate())
                .policyEndDate(dto.getPolicyEndDate())
                .financialYear(dto.getFinancialYear())

                .premium(dto.getPremium())
                .requestedAmount(dto.getRequestedAmount())
                .maxEligibleGrant(dto.getMaxEligibleGrant())
                .nhaShare(dto.getNhaShare())
                .tranche(dto.getTranche())


                .releaseTillDate(dto.getReleaseTillDate())
                .eSignStatusStateCeo(dto.getESignStatusStateCeo())
                .stateShare(dto.getStateShare())
                .statusDescription(getDefaultStatus(2))
                .remarks(Optional.ofNullable(dto.getRemarks()).orElse(""))
                .bankMappedWithPfms(Optional.ofNullable(dto.getBankMappedWithPfms()).orElse(false))
                .positiveBalance(Optional.ofNullable(dto.getPositiveBalance()).orElse(false))
                .totalStateShare(dto.getTotalStateShare())
                .schemeName(dto.getSchemeName())
                .schemeId(dto.getSchemeId())


                .build();
        if(dto.getNewBeneficiaryInstate()!=null)
            grantRequests.setNewBeneficiaryInstate(dto.getNewBeneficiaryInstate());
        if(dto.getOldBeneficiaryInState()!=null)
            grantRequests.setOldBeneficiaryInstate(dto.getOldBeneficiaryInState());
        if(dto.getTotalFamiliesCoveredInStateAsPerMou()!=null)
            grantRequests.setTotalFamiliesCoveredInStateAsPerMou(dto.getTotalFamiliesCoveredInStateAsPerMou());
        if(dto.getTotalEligibleAshaAwwAwhFamilies()!=null)
        {
            grantRequests.setTotalEligibleAshaAwwAwhFamilies(dto.getTotalEligibleAshaAwwAwhFamilies());
        }
        if(dto.getTotalBeneficiaryCount()!=null)
            grantRequests.setTotalBeneficiaryCount(dto.getTotalBeneficiaryCount());
        if(dto.getPmjayBeneficiaryCount()!=null)
        {
            grantRequests.setPmjayBeneficiaryCount(dto.getPmjayBeneficiaryCount());
        }
        return grantRequests;

    }

    private GrantRequests updateExistingGrantRequest(GrantRequests existingRequest, GrantRequestInputDto dto) {

        existingRequest.setState(getState(dto.getStateId()));
        existingRequest.setUser(getUser(dto.getUserId()));
        existingRequest.setProposalType(getProposalType(dto.getProposalTypeId()));
        existingRequest.setImplementationMode(getImplementationType(dto.getImplementationModeId()));
        existingRequest.setInsuranceCompany(dto.getInsuranceCompany());
        existingRequest.setPolicyStartDate(dto.getPolicyStartDate());
        existingRequest.setPolicyEndDate(dto.getPolicyEndDate());
        existingRequest.setFinancialYear(dto.getFinancialYear());
        existingRequest.setTranche(dto.getTranche());
        existingRequest.setPmjayBeneficiaryCount(dto.getPmjayBeneficiaryCount());
        existingRequest.setTotalBeneficiaryCount(dto.getTotalBeneficiaryCount());
        existingRequest.setPremium(dto.getPremium());
        existingRequest.setNhaShare(dto.getNhaShare());
        existingRequest.setMaxEligibleGrant(dto.getMaxEligibleGrant());
        existingRequest.setReleaseTillDate(dto.getReleaseTillDate());
        existingRequest.setRequestedAmount(dto.getRequestedAmount());
        existingRequest.setReleaseTillDate(dto.getReleaseTillDate());
        existingRequest.setSchemeId(dto.getSchemeId());
        existingRequest.setSchemeName(dto.getSchemeName());

        existingRequest.setStateShare(dto.getStateShare());
        existingRequest.setESignStatusStateCeo(dto.getESignStatusStateCeo());
        if(dto.getRemarks()!=null) {
            existingRequest.setRemarks(dto.getRemarks());
        }
        if(dto.getBankMappedWithPfms()!=null)
        {
        existingRequest.setBankMappedWithPfms(dto.getBankMappedWithPfms());}
        if(dto.getPositiveBalance()) {
            existingRequest.setPositiveBalance(dto.getPositiveBalance());
        }
        existingRequest.setTotalStateShare(dto.getTotalStateShare());
        existingRequest.setStatusDescription(getDefaultStatus(2));
        if(dto.getTotalFamiliesCoveredInStateAsPerMou()!=null)
        existingRequest.setTotalFamiliesCoveredInStateAsPerMou(dto.getTotalFamiliesCoveredInStateAsPerMou());
        if(dto.getTotalEligibleAshaAwwAwhFamilies()!=null)

        existingRequest.setTotalEligibleAshaAwwAwhFamilies(dto.getTotalEligibleAshaAwwAwhFamilies());
        if(dto.getNewBeneficiaryInstate()!=null)
            existingRequest.setNewBeneficiaryInstate(dto.getNewBeneficiaryInstate());
        if(dto.getOldBeneficiaryInState()!=null)
        existingRequest.setOldBeneficiaryInstate(dto.getOldBeneficiaryInState());
        return existingRequest;
    }

//    public void saveToDashboard(GrantRequests grantRequest) {
//        try {
//            Action action = getAction(4);
//StatusDescription statusDescription=getDefaultStatus(2);
//            Dashboard dashboard = Dashboard.builder()
//                    .requestId(grantRequest.getRequestId())
//                    .state(getState(grantRequest.getState().getId()))
//                    .previousRole(getRole(1))
//                    .stateCeoStatus(action)
//                    .finalFlowStatus(statusDescription)
//                    .currentRole(getRole(2))
//                    .build();
//            iDashboardRepo.save(dashboard);
//        }
//        catch(Exception e)
//        {
//            throw new RuntimeException("Error occured while saving to Dashboard" + e.toString());
//        }
//    }
public void saveToDashboard(
        String requestId,
        Integer stateId,
        Integer proposalTypeId,
        Integer previousRoleId,
        Integer currentRoleId,
        Integer stateCeoStatusId,
        Integer nhaStateCoordinatorStatusId,
        Integer nhaReviewerStatusId,
        Integer finalFlowStatusId
) {
    try {
        // Fetching related entities
        States state = (stateId != null) ? getState(stateId) : null;
        ProposalType proposalType = (proposalTypeId != null) ? getProposalType(proposalTypeId) : null;
        Roles previousRole = (previousRoleId != null) ? getRole(previousRoleId) : null;
        Roles currentRole = (currentRoleId != null) ? getRole(currentRoleId) : null;
        Action stateCeoStatus = (stateCeoStatusId != null) ? getAction(stateCeoStatusId) : null;
        Action nhaStateCoordinatorStatus = (nhaStateCoordinatorStatusId != null) ? getAction(nhaStateCoordinatorStatusId) : null;
        Action nhaReviewerStatus = (nhaReviewerStatusId != null) ? getAction(nhaReviewerStatusId) : null;
        StatusDescription finalFlowStatus = (finalFlowStatusId != null) ? getDefaultStatus(finalFlowStatusId) : null;

        // Creating and saving the Dashboard entity
        Dashboard dashboard = Dashboard.builder()
                .requestId(requestId)
                .state(state)
                .proposalType(proposalType)
                .previousRole(previousRole)
                .currentRole(currentRole)
                .stateCeoStatus(stateCeoStatus)
                .nhaStateCoordinatorStatus(nhaStateCoordinatorStatus)
                .nhaReviewerStatus(nhaReviewerStatus)
                .finalFlowStatus(finalFlowStatus)
                .createdAt(LocalDateTime.now()) // Explicitly setting timestamps
                .updatedAt(LocalDateTime.now())
                .build();

        iDashboardRepo.save(dashboard);
    } catch (Exception e) {
        e.printStackTrace(); // Log or handle exception properly
    }
}
    public void saveToWorkFlow(String requestId, Integer userId, Integer actionId, Integer proposalType, String remarks)
    {
        try {

            WorkFlow workFlow = WorkFlow.builder().
                    requestId(requestId).
                    user(getUser(userId)).
                    action(getAction(actionId)).
                    proposalType(getProposalType(proposalType)).
                    remarks(remarks).
                    build();
            iWorkFlow.save(workFlow);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Error ocurred while saving to Work Flow Dump " + e.toString());
        }

    }

    // Utility Methods for Fetching Entities Safely
    private States getState(Integer stateId) {
        return statesRepository.findById(stateId)
                .orElseThrow(() -> new RuntimeException("State not found"));
    }

    public User getUser(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public ProposalType getProposalType(Integer proposalTypeId) {
        return iProposalTypesRepo.findById(proposalTypeId)
                .orElseThrow(() -> new RuntimeException("Proposal Type not found"));
    }

    public
    ImplementationTypes getImplementationType(Integer implementationModeId) {
        return implementationTypes.findById(implementationModeId)
                .orElseThrow(() -> new RuntimeException("Implementation Mode not found"));
    }

    private StatusDescription getDefaultStatus(Integer id) {
        return iStatusDescription.findById(id)
                .orElseThrow(() -> new RuntimeException("Cannot find Status"));
    }

    private Roles getRole(Integer roleId) {
        return roles.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }
    private Action getAction(Integer actionID)
    {
        return iactionRepository.findById(actionID)
                .orElseThrow(() -> new RuntimeException("Action not found"));
    }
    private static final SecureRandom RANDOM = new SecureRandom();

    private String generateUniqueRequestId() {
        String requestId;
        do {
            int randomNumber = 1000000 + RANDOM.nextInt(9000000); // Generates a 7-digit number
            requestId = "NHA" + randomNumber;
        } while (iGrantRequestsRepo.existsByRequestId(requestId)); // Ensure uniqueness

        return requestId;
    }
public void saveToCalcFlow(GrantRequestInputDto dto1,GrantRequests savedRequest)
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
                .roleId(1)
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
                .roleId(1)
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
                .roleId(1)
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
                .roleId(1)
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

                .roleId(1)
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
                .roleId(1)
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
                .roleId(1)
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
                .roleId(1)
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
                .roleId(1)
                .build();
        vvsAdminRepo.save(vvsAdmin);
    }


}
}



