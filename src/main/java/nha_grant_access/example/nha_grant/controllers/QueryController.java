package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IQuery;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.GetActiveQuery;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.dto.RaiseQueryRequest;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
