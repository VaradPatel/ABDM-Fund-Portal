package nha_grant_access.example.nha_grant.service;

import nha_grant_access.example.nha_grant.Interface.IQuery;
import nha_grant_access.example.nha_grant.dto.Error;
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
    public List<GetActiveQuery> getActiveQueryByUserId(Integer stateId) {

        List<Object[]> results = iGrantRequestsRepo.findActiveQueryFromUserID(stateId);
       return  results.stream()
                .map(obj -> GetActiveQuery.builder()
                        .queryId((Integer) obj[0])
                        .requestId((String) obj[1])
                        .QueryComment((String) obj[2])
                        .queryDoc((String) obj[3])
                        .createdAt((Date) obj[4])
                        .userName((String) obj[5])  // Assuming this maps to u.name

                        // If added in your query
                        .build())
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public void respondToQueryBysha(GrantRequestInputDto dto) {
        try
        {





        }
        catch(Exception e)
        {
throw new RuntimeException(String.valueOf(new Error("error occured while responding to query",e.toString())));
        }


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
            if(request.getRoleId()==3 && request.getIsStateCeo()==1) //state cord to state ceo
            {
                query.setQueryToRoleID(2);
                iGrantRequestsRepo.updateStatusDescription(request.getRequestId(), 11);
            }
            else {
                WorkFlowConfiguration workFlowConfiguration = iWorkFlowConfRepo.findByActionPerformedIdAndActionPerformedById(2, request.getRoleId());
                if (request.getIsStateCeo() != null && request.getIsStateCeo() == 1) {
                    query.setQueryToRoleID(2);
                    iGrantRequestsRepo.updateStatusDescription(request.getRequestId(), 10);

                } else {
                    iGrantRequestsRepo.updateStatusDescription(request.getRequestId(), workFlowConfiguration.getStatusDescription().getId());

                }
            }
            grantRequestService.saveToWorkFlow(request.getRequestId(), request.getUserId(),2,request.getProposalTypeId(),request.getQuery());

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
