package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.ImplementationTrustNhaPayementDetails;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.repository.IAdminCalcRepo;
import nha_grant_access.example.nha_grant.repository.ImplementInsuCalcRepo;
import nha_grant_access.example.nha_grant.repository.ImplementTrustCalcRepo;
import nha_grant_access.example.nha_grant.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/calc")
@Slf4j
public class CalculationController {
    @Autowired
    CalculationService calculationService;
    @Autowired
    IAdminCalcRepo iAdminCalcRepo;
    @Autowired
    ImplementTrustCalcRepo implementTrustCalcRepo;
    @Autowired
    ImplementInsuCalcRepo implementInsuCalcRepo;
    @PostMapping("/implementation/trust")
    public ResponseEntity<?>ImplementationTrust(@Valid @RequestBody ImplementationTrustNhaPayementDetails implementationNhaPayementDetails)
    {
        try
        {
ImplementationTrustNhaPayementDetails implementationNhaPayementDetails1=calculationService.implementationTruestCalc(implementationNhaPayementDetails);
return ResponseEntity.ok().body(implementationNhaPayementDetails1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
          return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @PostMapping("/implementation/insurance")
    public ResponseEntity<?>ImplementationInsuranceHybrid(@Valid @RequestBody ImplementationInsurancePayementDetails implementationInsurancePayementDetails)
    {
        try
        {
            ImplementationInsurancePayementDetails implementationNhaPayementDetails1=calculationService.implementInsuCalc(implementationInsurancePayementDetails);
            return ResponseEntity.ok().body(implementationNhaPayementDetails1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @PostMapping("/administrative")
    public ResponseEntity<?>Administrative(@Valid @RequestBody AdminNhaPaymentDetails adminNhaPaymentDetails)
    {
        try
        {
AdminNhaPaymentDetails adminNhaPaymentDetails1=calculationService.administrativeCalc(adminNhaPaymentDetails);
            return ResponseEntity.ok().body(adminNhaPaymentDetails1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @GetMapping("/getAdministrative/{requestId}")
            public ResponseEntity<?>getAdministrativeCalcByRequestId(@PathVariable("requestId") String requestId)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            return ResponseEntity.ok().body(iAdminCalcRepo.findByRequestId(requestId));
        }
        catch (Exception e)
        {
            {
                log.error(e.toString());
                return   ResponseEntity.internalServerError().body(new Error("Error while fetching calculation details", e.toString()));
            }
        }

    }
    @GetMapping("/getImplementTrust/{requestId}")
    public ResponseEntity<?>getImplementTrustCalcByRequestId(@PathVariable("requestId") String requestId)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            return ResponseEntity.ok().body(implementTrustCalcRepo.findByRequestId(requestId));
        }
        catch (Exception e)
        {
            {
                log.error(e.toString());
                return   ResponseEntity.internalServerError().body(new Error("Error while fetching calculation details", e.toString()));
            }
        }

    }
    @GetMapping("/getImplementInsur/{requestId}")
    public ResponseEntity<?>getImplementInsuranceCalcByRequestId(@PathVariable("requestId") String requestId)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            return ResponseEntity.ok().body(implementInsuCalcRepo.findByRequestId(requestId));
        }
        catch (Exception e)
        {
            {
                log.error(e.toString());
                return   ResponseEntity.internalServerError().body(new Error("Error while fetching calculation details", e.toString()));
            }
        }

    }
}
