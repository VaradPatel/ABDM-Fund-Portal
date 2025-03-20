package nha_grant_access.example.nha_grant.controllers;

import lombok.RequiredArgsConstructor;
import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.dto.AllGrantRequest;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.dto.Test;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import nha_grant_access.example.nha_grant.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/grant-requests")
@RequiredArgsConstructor
public class GrantRequestController {
    @Autowired
    IGrantRequests iGrantRequests;
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepo userRepo;
    @PostMapping("/add")
    public ResponseEntity<?> createGrantRequest(@RequestBody @Valid GrantRequestInputDto grantRequestInputDTO) {
        try {
//
//            if (token.startsWith("Bearer ")) {
//                token = token.substring(7);
//            }
//
//            // Extract email from token
//            String email = jwtUtil.extractUsername(token);
//            String role= jwtUtil.extractRoleId(token);

//            User user = userRepo.findByEmail(email)
//                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
//


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
