package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.Interface.IQuery;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.GetActiveQuery;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QueryService implements IQuery {
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;

    @Override
    public List<GetActiveQuery> getActiveQueryByUserId(Integer userID) {

        List<Object[]> results = iGrantRequestsRepo.findActiveQueryFromUserID(userID);
        return results.stream()
                .map(obj -> new GetActiveQuery(
                        (String) obj[0],  // requestId
                        (String) obj[1],  // queryComment
                        (String) obj[2]   // queryDoc
                ))
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void respondToQueryBysha(GrantRequestInputDto dto) {
//        try
//        {
//            Optional<GrantRequests> existingGrant = iGrantRequestsRepo.findByRequestId(dto.getRequestId());
//           if(existingGrant.isPresent())
//           {
//               GrantRequests grantRequest = existingGrant.get();
//               grantRequest.setRequestId(dto.getRequestId());
//               grantRequest.setState(stateRepository.findById(dto.getStateId()).orElseThrow(() -> new IllegalArgumentException("Invalid stateId")));
//               grantRequest.setUser(userRepository.findById(dto.getUserId()).orElseThrow(() -> new IllegalArgumentException("Invalid userId")));
//               grantRequest.setProposalType(proposalTypeRepository.findById(dto.getProposalTypeId()).orElseThrow(() -> new IllegalArgumentException("Invalid proposalTypeId")));
//               grantRequest.setImplementationMode(implementationTypesRepository.findById(dto.getImplementationModeId()).orElseThrow(() -> new IllegalArgumentException("Invalid implementationModeId")));
//               grantRequest.setInsuranceCompany(dto.getInsuranceCompany());
//               grantRequest.setPolicyStartDate(dto.getPolicyStartDate());
//               grantRequest.setPolicyEndDate(dto.getPolicyEndDate());
//               grantRequest.setFinancialYear(dto.getFinancialYear());
//               grantRequest.setTranche(dto.getTranche());
//               grantRequest.setPmjayBeneficiaryCount(dto.getPmjayBeneficiaryCount());
//               grantRequest.setTotalBeneficiaryCount(dto.getTotalBeneficiaryCount());
//               grantRequest.setPremium(dto.getPremium());
//               grantRequest.setNhaShare(dto.getNhaShare());
//               grantRequest.setMaxEligibleGrant(dto.getMaxEligibleGrant());
//               grantRequest.setReleaseTillDate(dto.getReleaseTillDate());
//               grantRequest.setRequestedAmount(dto.getRequestedAmount());
//               grantRequest.setStateShare(dto.getStateShare());
//               grantRequest.setESignStatusStateCeo(dto.getESignStatusStateCeo());
//
//               iGrantRequestsRepo.save(grantRequest);
//
//               //save To dashboard
//               //save To workflow
//               //save To query
//           }
//
//        }
//        catch(Exception e)
//        {
//throw new RuntimeException(String.valueOf(new Error("error occured while responding to query",e.toString())));
//        }
//
//
    }
}
