package nha_grant_access.example.nha_grant.controllers;

import lombok.RequiredArgsConstructor;
import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.dto.AllGrantRequest;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.dto.Test;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigInteger;
import java.util.List;


@RestController
@RequestMapping("/grant-requests")
@RequiredArgsConstructor
public class GrantRequestController {
    @Autowired
    IGrantRequests iGrantRequests;
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;

    @PostMapping("/add")
    public ResponseEntity<?> createGrantRequest(@RequestBody @Valid GrantRequestInputDto grantRequestInputDTO) {
        try {
            GrantRequestInputDto savedGrantRequest = iGrantRequests.saveGrantRequest(grantRequestInputDTO);
            return ResponseEntity.ok(savedGrantRequest);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @GetMapping("/get-all/{userId}")
    public ResponseEntity<?> getAllGrantRequest(@PathVariable("userId") Integer userId) {
        try {
            List<AllGrantRequest> allGrantRequests = iGrantRequests.getAllGrantRequest(userId);
            return ResponseEntity.ok(allGrantRequests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }
    @GetMapping("/get/{requestId}")
    public ResponseEntity<?> getGrantRequestByRequestId(@PathVariable("requestId") String requestId) {
        try {
            GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
            return ResponseEntity.ok(grantRequests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }

    }
}
