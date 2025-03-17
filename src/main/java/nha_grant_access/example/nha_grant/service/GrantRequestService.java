package nha_grant_access.example.nha_grant.service;

import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.dto.AllGrantRequest;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.entity.*;
import nha_grant_access.example.nha_grant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
    private IWorkFlow iWorkFlow;
    @Autowired
    private IRolesRepository roles;
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

    @Override
    @Transactional
    public GrantRequestInputDto saveGrantRequest(GrantRequestInputDto dto) {
        try {
            if (dto.getRequestId() != null) {
                // Update Existing Request
                GrantRequests existingRequest = iGrantRequestsRepo.findGrantRequestByRequestId(dto.getRequestId());
                if (existingRequest == null) {
                    throw new RuntimeException("Invalid RequestID");
                }
                updateExistingGrantRequest(existingRequest, dto);
                iGrantRequestsRepo.save(existingRequest);
                log.info("edited the grant_requests " + existingRequest.toString());

            } else {
                // Create New Request
                GrantRequests newRequest = mapToEntity(dto);
                GrantRequests savedRequest = iGrantRequestsRepo.save(newRequest);
                saveToDashboard(savedRequest);
                log.info("saved the grant_requests " + savedRequest.toString());
                //save to user dump
                saveToWorkFlow(savedRequest.getRequestId(),savedRequest.getUser().getId(),1,savedRequest.getProposalType().getId());
            }
            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Not able to process your request", e);
        }
    }

    @Override
    public List<AllGrantRequest> getAllGrantRequest(Integer userId) {
        return iGrantRequestsRepo.findAllGrantRequest(userId).stream()
                .map(gr -> AllGrantRequest.builder()
                        .requestId(gr.getRequestId())
                        .dateRequested(gr.getCreatedAt())
                        .releaseAmount(gr.getReleasedAmount())
                        .requestedAmount(gr.getRequestedAmount())
                        .requestStatus(Optional.ofNullable(gr.getStatusDescription())
                                .map(StatusDescription::getDescription)
                                .orElse(""))
                        .build())
                .collect(Collectors.toList());
    }

    private GrantRequests mapToEntity(GrantRequestInputDto dto) {
        return GrantRequests.builder()
                .requestId(generateUniqueRequestId())
                .state(getState(dto.getStateId()))
                .user(getUser(dto.getUserId()))
                .proposalType(getProposalType(dto.getProposalTypeId()))
                .implementationMode(getImplementationType(dto.getImplementationModeId()))
                .insuranceCompany(dto.getInsuranceCompany())
                .policyStartDate(dto.getPolicyStartDate())
                .policyEndDate(dto.getPolicyEndDate())
                .financialYear(dto.getFinancialYear())
                .pmjayBeneficiaryCount(dto.getPmjayBeneficiaryCount())
                .premium(dto.getPremium())
                .requestedAmount(dto.getRequestedAmount())
                .maxEligibleGrant(dto.getMaxEligibleGrant())
                .nhaShare(dto.getNhaShare())
                .tranche(dto.getTranche())
                .totalBeneficiaryCount(dto.getTotalBeneficiaryCount())
                .releaseTillDate(dto.getReleaseTillDate())
                .eSignStatusStateCeo(dto.getESignStatusStateCeo())
                .stateShare(dto.getStateShare())
                .statusDescription(getDefaultStatus(2))
                .build();
    }

    private void updateExistingGrantRequest(GrantRequests existingRequest, GrantRequestInputDto dto) {

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
        existingRequest.setReleasedAmount(dto.getReleaseTillDate());
        existingRequest.setStateShare(dto.getStateShare());
        existingRequest.setESignStatusStateCeo(dto.getESignStatusStateCeo());
        existingRequest.setStatusDescription(getDefaultStatus(2));
    }

    public void saveToDashboard(GrantRequests grantRequest) {
        try {
            Action action = getAction(4);
StatusDescription statusDescription=getDefaultStatus(2);
            Dashboard dashboard = Dashboard.builder()
                    .requestId(grantRequest.getRequestId())
                    .state(getState(grantRequest.getState().getId()))
                    .previousRole(getRole(1))
                    .stateCeoStatus(action)
                    .finalFlowStatus(statusDescription)
                    .currentRole(getRole(2))
                    .build();
            iDashboardRepo.save(dashboard);
        }
        catch(Exception e)
        {
            throw new RuntimeException("Error occured while saving to Dashboard" + e.toString());
        }
    }
    public void saveToWorkFlow(String requestId, Integer userId, Integer actionId, Integer proposalType)
    {
        try {

            WorkFlow workFlow = WorkFlow.builder().
                    requestId(requestId).
                    user(getUser(userId)).
                    action(getAction(actionId)).
                    proposalType(getProposalType(proposalType)).
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

    private User getUser(Integer userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ProposalType getProposalType(Integer proposalTypeId) {
        return iProposalTypesRepo.findById(proposalTypeId)
                .orElseThrow(() -> new RuntimeException("Proposal Type not found"));
    }

    private ImplementationTypes getImplementationType(Integer implementationModeId) {
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



}
