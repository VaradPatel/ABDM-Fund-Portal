package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.Interface.IQuery;
import nha_grant_access.example.nha_grant.dto.GetActiveQuery;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.dto.RaiseQueryRequest;
import nha_grant_access.example.nha_grant.entity.Queries;
import nha_grant_access.example.nha_grant.entity.WorkFlowConfiguration;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.IQueries;
import nha_grant_access.example.nha_grant.repository.IWorkFlowConfRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueryService implements IQuery {
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;
    @Autowired
    IQueries iQueries;
    @Autowired
    GrantRequestService grantRequestService;

    @Autowired
    IWorkFlowConfRepo iWorkFlowConfRepo;

    @Override
    public List<GetActiveQuery> getActiveQueryByUserId(Integer userID) {

        List<Object[]> results = iGrantRequestsRepo.findActiveQueryFromUserID(userID);
        return results.stream()
                .map(obj -> new GetActiveQuery(
                        (Integer) obj[0],
                        (String) obj[1],  // requestId
                        (String) obj[2],  // queryComment
                        (String) obj[3],
        (Date)obj[4],
                        (String) obj[5]// queryDoc
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

    @Override
    @Transactional
    public Queries raiseQuery(RaiseQueryRequest request) {
        try {

            Queries query = Queries.builder()
                    .requestId(request.getRequestId())
                    .active(true)
                    .queryComment(request.getQuery())

                    .queryUser(grantRequestService.getUser(request.getUserId()))


                    .build();
            WorkFlowConfiguration workFlowConfiguration = iWorkFlowConfRepo.findByActionPerformedIdAndActionPerformedById(2, request.getRoleId());
            iGrantRequestsRepo.updateStatusDescription(request.getRequestId(),workFlowConfiguration.getStatusDescription().getId());
            grantRequestService.saveToWorkFlow(request.getRequestId(), request.getUserId(),2,request.getProposalTypeId());

            // grantRequestService.saveToDashboard(request.getRequestId(),request.getStateId(),request.getProposalTypeId(), request.getRoleId(),workFlowConfiguration.getAssignTo().getId(),);
//saveToDashboard
//Save To Query

            return iQueries.save(query);
            //return  query;


        } catch (Exception e) {
            throw new RuntimeException(e.toString());
        }
    }
}
