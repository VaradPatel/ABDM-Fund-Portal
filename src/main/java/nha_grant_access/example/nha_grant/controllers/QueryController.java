package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IQuery;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.IQueries;
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
    IQueries iQueries;
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
            return ResponseEntity.ok().body("Query Raised Successfully");
        }
        catch(Exception e)
        {
         return ResponseEntity.internalServerError().body(new Error("Error occured while raising query",e.toString()));
        }
    }


    @PostMapping("/query-respond")
    public ResponseEntity<?>respondToQuery(GrantRequestInputDto grantRequestInputDto)
    {
        Optional<GrantRequests> existingGrant = iGrantRequestsRepo.findByRequestId(grantRequestInputDto.getRequestId());
        if(!existingGrant.isPresent())
        {
            return ResponseEntity.badRequest().body(new Error("Request Id is not present","Request Id is not present"));
        }
   return ResponseEntity.ok().body("Query Responded Successfully");
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
