package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.Interface.IQuery;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.entity.Queries;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.entity.WorkFlowConfiguration;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.IQueries;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import nha_grant_access.example.nha_grant.service.GrantRequestService;
import nha_grant_access.example.nha_grant.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QueryController {
    @Autowired
    IQuery iQuery;
    @Autowired
    private IGrantRequestsRepo iGrantRequestsRepo;
    @Autowired
    private IGrantRequests iGrantRequests;
    @Autowired
    IQueries iQueries;
    @Autowired
    UserRepo userRepo;

    @Autowired
    GrantRequestService grantRequestService;
    @Autowired
    OtpService otpService;

    @GetMapping("/get-active-query/{userId}")
    public ResponseEntity<?> getActiveQueryRaised(@PathVariable("userId") Integer userId) {
        try {
            List<GetActiveQuery> getActiveQuery = iQuery.getActiveQueryByUserId(userId);
            return ResponseEntity.ok().body(getActiveQuery);
        }
        catch(Exception e)
        {
            log.info("error while fetching active queries "+ e.toString());
            return ResponseEntity.internalServerError().body("Error while fetching active queries "+ e.toString());
        }
    }
    @GetMapping("/stateceo-getactivequery/{stateId}")
    @PreAuthorize("hasAuthority('State CEO') ")
    public ResponseEntity<?> getStateCeoActiveQueryRaised(@PathVariable("stateId") Integer StateId) {
        try {
            List<Object[]> results= iGrantRequestsRepo.findStateActiveQueryFromStateID(StateId);
            List<GetActiveQuery> getActiveQuery = results.stream()
                    .map(obj -> GetActiveQuery.builder()
                            .queryId((Integer) obj[0])
                            .requestId((String) obj[1])
                            .QueryComment((String) obj[2])
                            .queryDoc((String) obj[3])
                            .createdAt((Date) obj[4])
                            .userName((String) obj[5])
                            .stateId((Integer) obj[6])
                            .stateName((String) obj[7])
                            .proposalTypeId((Integer) obj[8])

                             // If added in your query
                            .build())
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(getActiveQuery);
        }
        catch(Exception e)
        {
            log.info("error while fetching active queries "+ e.toString());
            return ResponseEntity.internalServerError().body("Error while fetching active queries "+ e.toString());
        }
    }


    @PostMapping("/raise-query")
            public ResponseEntity<?> raiseQuery(@Valid @RequestBody RaiseQueryRequest request)
    {
        try {
            iQuery.raiseQuery(request);
            if(request.getRoleId()==2)
            {
                Optional<GrantRequests> grantRequests=iGrantRequestsRepo.findByRequestId(request.getRequestId());
                otpService.ApplicationQueryRaiseMsgToSha(request.getRequestId(),grantRequests.get().getProposalType().getId(),grantRequests.get().getState().getId(), Math.toIntExact(grantRequests.get().getImplementationMode().getId()),grantRequests.get().getUser().getId());
            }
            if(request.getRoleId()==4)
            {
                Optional<GrantRequests> grantRequests=iGrantRequestsRepo.findByRequestId(request.getRequestId());
                otpService.ApplicationQueryRaiseMsgToCeo(request.getRequestId(),grantRequests.get().getProposalType().getId(),grantRequests.get().getState().getId(), Math.toIntExact(grantRequests.get().getImplementationMode().getId()),grantRequests.get().getUser().getId());
if(request.getIsStateCeo()==1)
{
    byte [] bytes=null;
    iGrantRequestsRepo.updateEsignStatusByRequestID(request.getRequestId(), bytes,"",false);
}
            }
            return ResponseEntity.ok().body(new SuccessResponse("Query Raised Successfully"));
        }
        catch(Exception e)
        {
         return ResponseEntity.internalServerError().body(new Error("Error occured while raising query",e.toString()));
        }
    }


    @PostMapping("/query-response")
    @PreAuthorize("hasAuthority('SHA Finance Division Individual')")

    public ResponseEntity<?>respondToQuery(@Valid @RequestBody GrantRequestInputDto grantRequestInputDto) {
        try {
            Optional<GrantRequests> existingGrant = iGrantRequestsRepo.findByRequestId(grantRequestInputDto.getRequestId());
            if (!existingGrant.isPresent()) {
                return ResponseEntity.badRequest().body(new Error("Request Id is Invalid" + grantRequestInputDto.getRequestId(), "Request Id is Invalid"));
            }
            Optional<Queries>existingQuery=iQueries.findById(grantRequestInputDto.getQueryId());
            Optional<User> user =userRepo.findById(grantRequestInputDto.getUserId());
            if(!existingQuery.isPresent())
            {
                return ResponseEntity.badRequest().body(new Error("Query Id is Invalid", "Query Id is not present"));

            }
            if(grantRequestInputDto.getUserId()==null || user.isEmpty())
            {
                return ResponseEntity.badRequest().body(new Error(" User Id is Invalid", "User Id is not present"));

            }
            GrantRequestInputDto savedGrantRequest = iGrantRequests.saveGrantRequest(grantRequestInputDto,true);

            existingQuery.get().setQueryResponseComment(grantRequestInputDto.getQueryResponse());
existingQuery.get().setActive(false);
            existingQuery.get().setResponseUser(user.get());
            iQueries.save(existingQuery.get());
return  ResponseEntity.ok().body(new SuccessResponse("Query Responded Succesfully"));


        }
        catch (Exception e)
        {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error while responding to Query" ,e.toString()));
        }


    }
    @PostMapping("/query-action/{actionId}")
    public ResponseEntity<?>forwardQuery(@Valid @RequestBody RaiseQueryRequest request , @PathVariable("actionId") Integer actionId) {
        try
        {
            Queries query = Queries.builder()
                    .requestId(request.getRequestId())
                    .active(true)
                    .queryComment(request.getQuery())

                    .queryUser(grantRequestService.getUser(request.getUserId()))


                    .build();
            if(actionId==1) {
                iGrantRequestsRepo.updateStatusDescription(request.getRequestId(), 10);
                grantRequestService.saveToWorkFlow(request.getRequestId(), request.getUserId(), 2, request.getProposalTypeId(), request.getQuery());
                iGrantRequestsRepo.updateESignStatus(request.getRequestId(),false);
                iQueries.save(query);
                Optional<GrantRequests> grantRequests=iGrantRequestsRepo.findByRequestId(request.getRequestId());
                otpService.ApplicationQueryRaiseMsgToCeo(request.getRequestId(),grantRequests.get().getProposalType().getId(),grantRequests.get().getState().getId(), Math.toIntExact(grantRequests.get().getImplementationMode().getId()),grantRequests.get().getUser().getId());
                byte [] bytes=null;
                iGrantRequestsRepo.updateEsignStatusByRequestID(request.getRequestId(), bytes,"",false);
            }
            else if (actionId==3){
                iGrantRequestsRepo.updateStatusDescription(request.getRequestId(), 5);
                grantRequestService.saveToWorkFlow(request.getRequestId(), request.getUserId(), 7, request.getProposalTypeId(), request.getQuery());
                iQueries.save(query);
            }
            else if(actionId==2)
            {
                iGrantRequestsRepo.updateStatusDescription(request.getRequestId(), 4);
                grantRequestService.saveToWorkFlow(request.getRequestId(), request.getUserId(), 7, request.getProposalTypeId(), request.getQuery());
              query.setActive(false);
              iQueries.save(query);
              //set all this trail as false;
                iQueries.deactivateQueriesByRequestId(request.getRequestId());



            }
            else
            {
                iGrantRequestsRepo.updateStatusDescription(request.getRequestId(), 1);
                grantRequestService.saveToWorkFlow(request.getRequestId(), request.getUserId(), 7, request.getProposalTypeId(), request.getQuery());

                iQueries.save(query);

                Optional<GrantRequests> grantRequests=iGrantRequestsRepo.findByRequestId(request.getRequestId());
                otpService.ApplicationQueryRaiseMsgToSha(request.getRequestId(),grantRequests.get().getProposalType().getId(),grantRequests.get().getState().getId(), Math.toIntExact(grantRequests.get().getImplementationMode().getId()),grantRequests.get().getUser().getId());

                //set all this trail as false;

            }



            return  ResponseEntity.ok().body(new SuccessResponse("Query Raised Successfully"));
        }
        catch(Exception e)
        {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error while Raising Query" ,e.toString()));

        }

    }

        @GetMapping("/queryhistory/{userId}")

    public ResponseEntity<?> getGrantRequestsWithQueries(@PathVariable("userId") Integer userId) {
        try
        {

            List<Object[]> queries=iQueries.findWorkflowDetailsByUserId(userId);
            List<QueryHistory> queryHistoryList = queries.stream()
                    .map(row -> QueryHistory.builder()
                            .query((String) row[0])
                            .requestId((String) row[1])
                            .createdAt(((Timestamp) row[2]).toLocalDateTime())
                            .state((String) row[3])
                            .policyStartDate(LocalDate.from(((Timestamp) row[4] ).toLocalDateTime()))
                            .policyEndDate(LocalDate.from(((Timestamp) row[5] ).toLocalDateTime()))
                            .implementationTypes((String )grantRequestService.getProposalType((Integer) row[6]).getName())

                            .build())
                    .toList();

            return ResponseEntity.ok().body(queryHistoryList);


        }
        catch (Exception e)
        {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error ("Error While fetching the Query History ", e.toString() ));
        }
    }
@GetMapping("/statecord-getactivequery/{stateId}")
@PreAuthorize("hasAuthority('NHA State Co-ordinator') ")
public ResponseEntity<?> getStateCordActiveQueryRaised(@PathVariable("stateId") Integer StateId) {
    try {
        List<Object[]> results=null;

        if(StateId!=0) {
            results = iGrantRequestsRepo.findStateCordActiveQuery(Collections.singletonList(StateId));
        }
        else {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            User user = userRepo.findByEmail(name).get();


            List<Integer> StateIds = userRepo.findStateIdByRole(3, user.getId());
            results=iGrantRequestsRepo.findStateCordActiveQuery(StateIds);

        }
        List<GetActiveQuery> getActiveQuery = results.stream()
                .map(obj -> GetActiveQuery.builder()
                        .queryId((Integer) obj[0])
                        .requestId((String) obj[1])
                        .QueryComment((String) obj[2])
                        .queryDoc((String) obj[3])
                        .createdAt((Date) obj[4])
                        .userName((String) obj[5])  // Assuming this maps to u.name
                        .stateName((String) obj[6])
                        .proposalTypeId((Integer) obj[7])
                        .stateId((Integer) obj[8])
                        // If added in your query
                        .build())
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(getActiveQuery);
    }
    catch(Exception e)
    {
        log.info("error while fetching active queries "+ e.toString());
        return ResponseEntity.internalServerError().body("Error while fetching active queries "+ e.toString());
    }
}

    @GetMapping("/nhareviewer-getactivequery/{stateId}")
    @PreAuthorize("hasAuthority('NHA reviewer') ")
    public ResponseEntity<?> getNhaReviewerActiveQueryRaised(@PathVariable("stateId") Integer StateId) {
        try {
            List<Object[]> results=null;

          results=iGrantRequestsRepo.findNhaReviewerActiveQuery(StateId);

            List<GetActiveQuery> getActiveQuery=results.stream()
                    .map(obj -> new GetActiveQuery(
                            (Integer) obj[0],
                            (String) obj[1],  // requestId
                            (String) obj[2],  // queryComment
                            (String) obj[3],
                            (Date)obj[4],
                            (String) obj[5],
                            (String) obj[6],
                            (Integer) obj[7],
                            (Integer) obj[8]// queryDoc
                    ))
                    .collect(Collectors.toList());
            return ResponseEntity.ok().body(getActiveQuery);
        }
        catch(Exception e)
        {
            log.info("error while fetching active queries "+ e.toString());
            return ResponseEntity.internalServerError().body("Error while fetching active queries "+ e.toString());
        }
    }





}
