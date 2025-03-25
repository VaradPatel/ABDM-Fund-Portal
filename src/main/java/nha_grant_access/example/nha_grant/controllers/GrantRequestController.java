package nha_grant_access.example.nha_grant.controllers;

import lombok.RequiredArgsConstructor;
import nha_grant_access.example.nha_grant.Interface.IGrantRequests;
import nha_grant_access.example.nha_grant.dto.AllGrantRequest;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.GrantRequestInputDto;
import nha_grant_access.example.nha_grant.dto.Test;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import nha_grant_access.example.nha_grant.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
    //@PreAuthorize("hasAuthority('SHA Finance Division Individual')")
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
            return ResponseEntity.internalServerError().body(new Error("Failed to create Grant Request ",e.toString()));
        }
    }

    @GetMapping("/get-all/{stateId}")

   //@PreAuthorize("hasAuthority('SHA Finance Division Individual')")
    public ResponseEntity<?> getAllGrantRequest(@PathVariable("stateId") Integer stateId) {
        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication != null) {
//                System.out.println("Authenticated User: " + authentication.getName());
//                System.out.println("Roles: " + authentication.getAuthorities());
//            }
            List<AllGrantRequest> allGrantRequests = iGrantRequests.getAllGrantRequest(stateId);
            return ResponseEntity.ok(allGrantRequests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Error ("failed to fetch details ", e.toString()));
        }

    }
    @GetMapping("/get/{requestId}")
    public ResponseEntity<?> getGrantRequestByRequestId(@PathVariable("requestId") String requestId) {
        try {
            GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
            return ResponseEntity.ok(grantRequests);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(new Error("Failed to fetch grant request details", e.toString()));
        }

    }
}
