package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.ImplementationTrustNhaPayementDetails;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.repository.*;
import nha_grant_access.example.nha_grant.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/calc")
@Slf4j
public class CalculationController {
    @Autowired
    CalculationService calculationService;
    @Autowired
    IAdminCalcRepo iAdminCalcRepo;
    @Autowired
    IAashaHybrid iAashaHybrid;
    @Autowired
    ImplementTrustCalcRepo implementTrustCalcRepo;
    @Autowired
    ImplementInsuCalcRepo implementInsuCalcRepo;
    @Autowired
    IAashaAdmin iAashaAdmin;
    @Autowired
    IashaImplTrust iashaImplTrust;
    @Autowired
    IvvsImpleNew ivvsImpleNew;
    @PostMapping("/implementation/trust")
    public ResponseEntity<?>ImplementationTrust(@Valid @RequestBody ImplementationTrustNhaPayementDetails implementationNhaPayementDetails)
    {
        try
        {
ImplementationTrustNhaPayementDetails implementationNhaPayementDetails1=calculationService.implementationTruestCalc(implementationNhaPayementDetails);
if(implementationNhaPayementDetails1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
{
    ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
}
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
            if(implementationNhaPayementDetails1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            return ResponseEntity.ok().body(implementationNhaPayementDetails1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @PostMapping("/aasha/hybrid")
    public ResponseEntity<?>AashaHybrid(@Valid @RequestBody AashaHybrid aashaHybrid)
    {
        try
        {
            AashaHybrid implementationNhaPayementDetails1=calculationService.AashaHybridCalc(aashaHybrid);
            if(implementationNhaPayementDetails1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            return ResponseEntity.ok().body(implementationNhaPayementDetails1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @PostMapping("/aasha/admin")
    public ResponseEntity<?>AashaAdmin(@Valid @RequestBody AashaAdmin aashaAdmin)
    {
        try
        {
            AashaAdmin aashaAdmin1=calculationService.AashaAdminCalc(aashaAdmin);
            if(aashaAdmin1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            return ResponseEntity.ok().body(aashaAdmin1);

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
            if(adminNhaPaymentDetails1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
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
    @GetMapping("/getImplementInsurance/{requestId}")
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

    @PostMapping("/vvs/implementation-trust")
    public ResponseEntity<?>vvsImplementationNew(@Valid @RequestBody VVSImplementationNewBenef vvsImplementationNewBenef)
    {
        try
        {
            VVSImplementationNewBenef vvsImplementationNewBenef1=calculationService.vvsimplementationNewBenef(vvsImplementationNewBenef);
            if(vvsImplementationNewBenef1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            return ResponseEntity.ok().body(vvsImplementationNewBenef1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @PostMapping("/aasha/implementation-trust")
    public ResponseEntity<?>aashaImplementationNew(@Valid @RequestBody AashaImplementationTrust aashaImplementationTrust)
    {
        try
        {
             AashaImplementationTrust aashaImplementationTrust1=calculationService.aashaImplementationTrust(aashaImplementationTrust);
            if(aashaImplementationTrust.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            return ResponseEntity.ok().body(aashaImplementationTrust1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @GetMapping("/getAshaImpTrust/{requestId}")
    public ResponseEntity<?>getAshaTrustByRequestId(@PathVariable("requestId") String requestId)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            return ResponseEntity.ok().body(iashaImplTrust.findByRequestId(requestId));
        }
        catch (Exception e)
        {
            {
                log.error(e.toString());
                return   ResponseEntity.internalServerError().body(new Error("Error while fetching calculation details", e.toString()));
            }
        }

    }
    @GetMapping("/getVVSImpTrust/{requestId}")
    public ResponseEntity<?>getVVSTrustByRequestId(@PathVariable("requestId") String requestId)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            return ResponseEntity.ok().body(ivvsImpleNew.findByRequestId(requestId));
        }
        catch (Exception e)
        {
            {
                log.error(e.toString());
                return   ResponseEntity.internalServerError().body(new Error("Error while fetching calculation details", e.toString()));
            }
        }

    }
    @GetMapping("/getAashaImpHybrid/{requestId}")
    public ResponseEntity<?>getAashaHybrid(@PathVariable("requestId") String requestId)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            return ResponseEntity.ok().body(iAashaHybrid.findByRequestId(requestId));
        }
        catch (Exception e)
        {
            {
                log.error(e.toString());
                return   ResponseEntity.internalServerError().body(new Error("Error while fetching calculation details", e.toString()));
            }
        }

    }
    @GetMapping("/getAashaAdmin/{requestId}")
    public ResponseEntity<?>getAashaAdmin(@PathVariable("requestId") String requestId)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            return ResponseEntity.ok().body(iAashaAdmin.findByRequestId(requestId));
        }
        catch (Exception e)
        {
            {
                log.error(e.toString());
                return  ResponseEntity.internalServerError().body(new Error("Error while fetching calculation details", e.toString()));
            }
        }

    }
}
