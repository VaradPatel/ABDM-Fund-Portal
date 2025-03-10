package nha_grant_access.example.nha_grant.controllers;

import lombok.RequiredArgsConstructor;
import nha_grant_access.example.nha_grant.Interface.IQuery;
import nha_grant_access.example.nha_grant.dto.GetActiveQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController

@RequiredArgsConstructor
public class QueryController {
    @Autowired
    IQuery iQuery;
    @GetMapping("/get-active-query/{userId}")
    public ResponseEntity<?> getActiveQueryRaised(@PathVariable("userId") Integer userId) {
        List<GetActiveQuery> getActiveQuery=iQuery.getActiveQueryByUserId(userId);
        return ResponseEntity.ok().body(getActiveQuery);
    }
}
