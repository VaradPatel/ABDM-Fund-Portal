package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.dto.AllGrantRequest;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.entity.*;
import nha_grant_access.example.nha_grant.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GrantRequestService implements IGrantRequests {
 @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;
 @Autowired
    IstatesRepository statesRepository;
 @Autowired
 IRolesRepository roles;
 @Autowired
 IDashboardRepo iDashboardRepo;
 @Autowired
 UserRepo userRepo;
    @Autowired
    ImplementationTypesRepo implementationTypes ;
    @Autowired
    IProposalTypesRepo iProposalTypesRepo;
    @Autowired
    IStatusDescription iStatusDescription;



    @Override
    public GrantRequestInputDto saveGrantRequest(GrantRequestInputDto grantRequestInputDTO)
    {
        try {
            GrantRequests grantRequest = mapToEntity(grantRequestInputDTO);
            GrantRequests savedGrantRequest = iGrantRequestsRepo.save(grantRequest);
            //saveToDashboard
            saveToDashboard(savedGrantRequest);

            //saveToWorkFlow
            return  grantRequestInputDTO;
        }
        catch(Exception e)
        {
            throw new RuntimeException("Not able to process your request");
        }

    }

    @Override
    public List<AllGrantRequest> getAllGrantRequest(Integer userId) {

        List<GrantRequests> grantRequests=iGrantRequestsRepo.findAllGrantRequest(userId);

        List<AllGrantRequest> allGrantRequests = grantRequests.stream()
                .map(gr -> AllGrantRequest.builder()
                        .requestId(gr.getRequestId())
                        .dateRequested(gr.getCreatedAt())
                        .releaseAmount(gr.getReleasedAmount())
                        .requestedAmount(gr.getRequestedAmount())
                        .requestStatus(Optional.ofNullable(gr.getStatusDescription())
                                .map(StatusDescription::getDescription)
                                .orElse(""))
                        .build()
                )
                .toList();
        return allGrantRequests;
    }


    private GrantRequests mapToEntity(GrantRequestInputDto dto) {
        States state = statesRepository.findById(dto.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found"));
        User user = userRepo.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        ProposalType proposalType = iProposalTypesRepo.findById(dto.getProposalTypeId())
                .orElseThrow(() -> new RuntimeException("Proposal Type not found"));
        ImplementationTypes implementationMode = implementationTypes.findById(dto.getImplementationModeId())
                .orElseThrow(() -> new RuntimeException("Implementation Mode not found"));
        StatusDescription statusDescription = iStatusDescription.findById(2)
                .orElseThrow(() -> new RuntimeException("Cannot find Status"));
String requestId=UUID.randomUUID().toString();
        return GrantRequests.builder()
                        .requestId(requestId)
                .state(state)
                .user(user)
                .implementationMode(implementationMode)
                .proposalType(proposalType)
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
                .premium(dto.getPremium())
                .eSignStatusStateCeo(dto.getESignStatusStateCeo())
                .maxEligibleGrant(dto.getMaxEligibleGrant())
                .stateShare(dto.getStateShare())
                .statusDescription(statusDescription)


                                .

                build();


    }
    public void saveToDashboard(GrantRequests grantRequests )
    {
        States state = statesRepository.findById(grantRequests.getState().getId())
                .orElseThrow(() -> new RuntimeException("State not found"));
        Roles prev=roles.findById(1).orElseThrow(()->new RuntimeException("Role not found"));
        Roles curr=roles.findById(2).orElseThrow(()->new RuntimeException("Role not found"));

    Dashboard dashboard= Dashboard.builder()
                    .requestId(grantRequests.getRequestId())
        .state(state).previousRole(prev).currentRole(curr).




             build();
    iDashboardRepo.save(dashboard);
    }


}
