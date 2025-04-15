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
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.IQueries;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
    @PostMapping("/raise-query")
            public ResponseEntity<?> raiseQuery(@Valid @RequestBody RaiseQueryRequest request)
    {
        try {
            iQuery.raiseQuery(request);
            return ResponseEntity.ok().body(new SuccessResponse("Query Raised Successfully"));
        }
        catch(Exception e)
        {
         return ResponseEntity.internalServerError().body(new Error("Error occured while raising query",e.toString()));
        }
    }


    @PostMapping("/query-response")
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
    @GetMapping("/queryhistory-sha/{stateId}")

    public ResponseEntity<?> getGrantRequestsWithQueries(@PathVariable("stateId") Integer stateId) {
        try
        {

            List<Object[]> results=iQueries.findActiveQueriesShaFinance(stateId);
            List<QueryHistoryResponse>result=results.stream().map(obj -> new QueryHistoryResponse(
                    (String) obj[0],   // request_id
                    (BigDecimal) obj[1],   // requested_amount
                    (Date) obj[2],   // created_at
                    (String) obj[3],   // query_comment
                    (String) obj[4]    // query_response_comment
            )).toList();
            return ResponseEntity.ok().body(result);


        }
        catch (Exception e)
        {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error ("Error While fetching the Active Queries ", e.toString() ));
        }
    }

}
