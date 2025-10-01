package nha_grant_access.example.nha_grant.service;

import lombok.RequiredArgsConstructor;
import nha_grant_access.example.nha_grant.dto.OfflineProposalRequest;
import nha_grant_access.example.nha_grant.entity.*;
import nha_grant_access.example.nha_grant.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OfflineDataService {

    private final IOfflineDataRepo offlineDataRepository;
    private final IstatesRepository statesRepository;
    private final IProposalTypesRepo proposalTypeRepository;
    private final ImplementationTypesRepo implementationTypesRepository;

    @Transactional
    public OfflineData saveOfflineData(OfflineProposalRequest request) {
        OfflineData offlineData = convertToEntity(request);
        return offlineDataRepository.save(offlineData);
    }

    public OfflineData getOfflineDataById(Integer id) {
        return offlineDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Offline data not found with id: " + id));
    }

    @Transactional
    public OfflineData updateOfflineData(Integer id, OfflineProposalRequest request) {
        OfflineData existingData = getOfflineDataById(id);

        // Update fields
        existingData.setState(statesRepository.findById(request.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found")));
        existingData.setProposalType(proposalTypeRepository.findById(request.getProposalTypeId())
                .orElseThrow(() -> new RuntimeException("Proposal type not found")));
        existingData.setImplementationMode(implementationTypesRepository.findById(request.getImplementationModeId())
                .orElseThrow(() -> new RuntimeException("Implementation mode not found")));
        existingData.setPolicyStartDate(request.getPolicyStartDate());
        existingData.setPolicyEndDate(request.getPolicyEndDate());
        existingData.setFinancialYear(request.getFinancialYear());
        existingData.setTranche(request.getTranche());
        existingData.setRequestedAmount(request.getRequestedAmount());
        existingData.setReleasedAmount(request.getReleasedAmount());
        existingData.setAmountSc(request.getAmountSC());
        existingData.setAmountSt(request.getAmountST());
        existingData.setAmountGc(request.getAmountGC());
        existingData.setSchemeId(request.getSchemeId());
        existingData.setSchemeName(request.getSchemeName());

        return offlineDataRepository.save(existingData);
    }

    @Transactional
    public void deleteOfflineData(Integer id) {
        OfflineData data = getOfflineDataById(id);
        offlineDataRepository.delete(data);
    }

    private OfflineData convertToEntity(OfflineProposalRequest request) {
        States state = statesRepository.findById(request.getStateId())
                .orElseThrow(() -> new RuntimeException("State not found with id: " + request.getStateId()));

        ProposalType proposalType = proposalTypeRepository.findById(request.getProposalTypeId())
                .orElseThrow(() -> new RuntimeException("Proposal type not found with id: " + request.getProposalTypeId()));

        ImplementationTypes implementationMode = implementationTypesRepository.findById(request.getImplementationModeId())
                .orElseThrow(() -> new RuntimeException("Implementation mode not found with id: " + request.getImplementationModeId()));

        return OfflineData.builder()
                .state(state)
                .proposalType(proposalType)
                .implementationMode(implementationMode)
                .policyStartDate(request.getPolicyStartDate())
                .policyEndDate(request.getPolicyEndDate())
                .financialYear(request.getFinancialYear())
                .tranche(request.getTranche())
                .requestedAmount(request.getRequestedAmount())
                .releasedAmount(request.getReleasedAmount())
                .amountSc(request.getAmountSC())
                .amountSt(request.getAmountST())
                .amountGc(request.getAmountGC())
                .schemeId(request.getSchemeId())
                .schemeName(request.getSchemeName())
                .requestedDate(request.getRequested_Date())
                .releasedDate(request.getReleased_Date())
                .build();
    }
}