package nha_grant_access.example.nha_grant.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.Interface.IQuery;
import nha_grant_access.example.nha_grant.dto.GetActiveQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
public class QueryController {
    @Autowired
    IQuery iQuery;
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
//    @PostMapping("/raise-query")
//            public ResponseEntity<?> raiseQuery()
//    {
//
//    }


}
