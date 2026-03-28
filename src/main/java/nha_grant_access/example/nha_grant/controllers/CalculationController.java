package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.dto.*;
import nha_grant_access.example.nha_grant.dto.AashaAdmin;
import nha_grant_access.example.nha_grant.dto.AashaHybrid;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.ImplementationTrustNhaPayementDetails;
import nha_grant_access.example.nha_grant.dto.VvsAdmin;
import nha_grant_access.example.nha_grant.dto.VvsHybrid;
import nha_grant_access.example.nha_grant.entity.*;
import nha_grant_access.example.nha_grant.repository.*;
import nha_grant_access.example.nha_grant.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/calc")
@Slf4j
public class CalculationController {
    @Autowired
    CalculationService calculationService;
    @Autowired
    VvsAdminRepo vvsAdminRepo;
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
    IVvsHybrid iVvsHybrid;
    @Autowired
    IashaImplTrust iashaImplTrust;
    @Autowired
    ImplementTrustExcelService implementTrustExcelService;
    @Autowired
    AashaHybridExcelService aashaHybridExcelService;
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;

    @Autowired
    AashaAdminExcelService aashaAdminExcelService;
    @Autowired
    AdministrativeExcelService administrativeExcelService;

    @Autowired
    ImplementInsuranceExcelService implementInsuranceExcelService;
    @Autowired
    IvvsImpleNew ivvsImpleNew;

    @Autowired
    StateCordEditService service;
    @PostMapping("/implementation/trust")
    public ResponseEntity<?>ImplementationTrust(@Valid @RequestBody ImplementationTrustNhaPayementDetails implementationNhaPayementDetails ,  @RequestParam(value = "requestId", required = false) String requestId )
    {
        try
        {
ImplementationTrustNhaPayementDetails implementationNhaPayementDetails1=calculationService.implementationTruestCalc(implementationNhaPayementDetails);
if(implementationNhaPayementDetails1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
{
    ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
}
if(requestId!=null)
{
    stateCordEdit stateCordEdit=new stateCordEdit();
    stateCordEdit.setImplementationTrustNhaPayementDetails(implementationNhaPayementDetails1);
    GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
    service.saveToCalcFlow(stateCordEdit,grantRequests);
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
    public ResponseEntity<?>ImplementationInsuranceHybrid(@Valid @RequestBody ImplementationInsurancePayementDetails implementationInsurancePayementDetails, @RequestParam(value = "requestId", required = false) String requestId)
    {
        try
        {
            ImplementationInsurancePayementDetails implementationNhaPayementDetails1=calculationService.implementInsuCalc(implementationInsurancePayementDetails);
            if(implementationNhaPayementDetails1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            if(requestId!=null)
            {
                stateCordEdit stateCordEdit=new stateCordEdit();
                stateCordEdit.setImplementationInsurancePayementDetails(implementationNhaPayementDetails1);
                GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
                service.saveToCalcFlow(stateCordEdit,grantRequests);
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
    public ResponseEntity<?>AashaHybrid(@Valid @RequestBody AashaHybrid aashaHybrid, @RequestParam(value = "requestId", required = false) String requestId)
    {
        try
        {
            AashaHybrid implementationNhaPayementDetails1=calculationService.AashaHybridCalc(aashaHybrid);
            if(implementationNhaPayementDetails1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }if(requestId!=null)
        {
            stateCordEdit stateCordEdit=new stateCordEdit();
           stateCordEdit.setAashaHybrid(implementationNhaPayementDetails1);
            GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
            service.saveToCalcFlow(stateCordEdit,grantRequests);
        }
            return ResponseEntity.ok().body(implementationNhaPayementDetails1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @PostMapping("/aasha/adm")
    public ResponseEntity<?>AashaAdmin(@Valid @RequestBody AashaAdmin aashaAdmin,@RequestParam(value = "requestId", required = false) String requestId)
    {
        try
        {
            AashaAdmin aashaAdmin1=calculationService.AashaAdminCalc(aashaAdmin);
            if(aashaAdmin1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            if(requestId!=null)
            {
                stateCordEdit stateCordEdit=new stateCordEdit();
               stateCordEdit.setAashaAdmin(aashaAdmin1);
                GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
                service.saveToCalcFlow(stateCordEdit,grantRequests);
            }
            return ResponseEntity.ok().body(aashaAdmin1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @PostMapping("/adm")
    public ResponseEntity<?>Administrative(@Valid @RequestBody AdminNhaPaymentDetails adminNhaPaymentDetails , @RequestParam(value = "requestId", required = false) String requestId)
    {
        try
        {
            AdminNhaPaymentDetails adminNhaPaymentDetails1=calculationService.administrativeCalc(adminNhaPaymentDetails);
            if(adminNhaPaymentDetails1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            if(requestId!=null)
            {
                stateCordEdit stateCordEdit=new stateCordEdit();
                stateCordEdit.setAdminNhaPaymentDetails(adminNhaPaymentDetails1);
                GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
                service.saveToCalcFlow(stateCordEdit,grantRequests);
            }
            return ResponseEntity.ok().body(adminNhaPaymentDetails1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @GetMapping("/getAdm/{requestId}")
    public ResponseEntity<?> getAdministrativeCalcByRequestId(
            @PathVariable("requestId") String requestId,
            @RequestParam(value = "excel", required = false, defaultValue = "false") boolean excel) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();

            Optional<AdministrativeCalc> optional = iAdminCalcRepo.findByRequestId(requestId);

            // ✅ Handle no data
            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found for requestId: " + requestId);
            }

            AdministrativeCalc data = optional.get();

            // ✅ Excel case
            if (excel) {
                ByteArrayInputStream excelFile =
                        administrativeExcelService.generateExcel(List.of(data));

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=administrative_calc.xlsx")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(new InputStreamResource(excelFile));
            }

            // ✅ JSON
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError()
                    .body(new Error("Error while fetching calculation details", e.toString()));
        }
    }
    @GetMapping("/getImplementTrust/{requestId}")
    public ResponseEntity<?> getImplementTrustCalcByRequestId(
            @PathVariable("requestId") String requestId,
            @RequestParam(value = "excel", required = false, defaultValue = "false") boolean excel) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();

            ImplementTrustCalc data = implementTrustCalcRepo.findByRequestId(requestId).get();
            List<ImplementTrustCalc>lst=new ArrayList<>();
            lst.add(data);
            // ✅ If Excel requested
            if (excel) {
                ByteArrayInputStream excelFile = implementTrustExcelService.generateExcel(lst);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=implement_trust.xlsx")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(new InputStreamResource(excelFile));
            }

            // ✅ Default JSON response
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError()
                    .body(new Error("Error while fetching calculation details", e.toString()));
        }
    }
    @GetMapping("/getImplementInsurance/{requestId}")
    public ResponseEntity<?> getImplementInsuranceCalcByRequestId(
            @PathVariable("requestId") String requestId,
            @RequestParam(value = "excel", required = false, defaultValue = "false") boolean excel) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();

            Optional<ImplementInsuCalc> optional = implementInsuCalcRepo.findByRequestId(requestId);

            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found for requestId: " + requestId);
            }

            ImplementInsuCalc obj = optional.get();
            List<ImplementInsuCalc> data = List.of(obj);
            // ✅ Excel download
            if (excel) {
                ByteArrayInputStream excelFile = implementInsuranceExcelService.generateExcel(data);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=implement_insurance.xlsx")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(new InputStreamResource(excelFile));
            }

            // ✅ JSON response
            return ResponseEntity.ok(obj);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError()
                    .body(new Error("Error while fetching calculation details", e.toString()));
        }
    }

    @PostMapping("/vvs/implementation-trust")
    public ResponseEntity<?>vvsImplementationNew(@Valid @RequestBody VVSImplementationNewBenef vvsImplementationNewBenef , @RequestParam(value = "requestId", required = false) String requestId)
    {
        try
        {
            VVSImplementationNewBenef vvsImplementationNewBenef1=calculationService.vvsimplementationNewBenef(vvsImplementationNewBenef);
            if(vvsImplementationNewBenef1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            if(requestId!=null)
            {
                stateCordEdit stateCordEdit=new stateCordEdit();
                stateCordEdit.setVvsImplementationNewBenef(vvsImplementationNewBenef1);
                GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
                service.saveToCalcFlow(stateCordEdit,grantRequests);
            }
            return ResponseEntity.ok().body(vvsImplementationNewBenef1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @PostMapping("/vvs/implementation-hybrid")
    public ResponseEntity<?>vvsImplHybrid(@Valid @RequestBody VvsHybrid vvsHybrid , @RequestParam(value = "requestId", required = false) String requestId)
    {
        try
        {
           VvsHybrid vvsImplementationNewBenef1=calculationService.vvshybridcalc(vvsHybrid);
            if(vvsImplementationNewBenef1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            if(requestId!=null)
            {
                stateCordEdit stateCordEdit=new stateCordEdit();
              stateCordEdit.setVvsHybrid(vvsImplementationNewBenef1);
                GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
                service.saveToCalcFlow(stateCordEdit,grantRequests);
            }
            return ResponseEntity.ok().body(vvsImplementationNewBenef1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
            return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
    @PostMapping("/vvs/adm")
    public ResponseEntity<?>vvsImplAdmin(@Valid @RequestBody VvsAdmin vvsAdmin, @RequestParam(value = "requestId", required = false) String requestId)
    {
        try
        {
           VvsAdmin vvsImplementationNewBenef1=calculationService.VvsAdmin(vvsAdmin);
            if(vvsImplementationNewBenef1.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            if(requestId!=null)
            {
                stateCordEdit stateCordEdit=new stateCordEdit();
                stateCordEdit.setVvsAdmin(vvsImplementationNewBenef1);
              GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
                service.saveToCalcFlow(stateCordEdit,grantRequests);
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
    public ResponseEntity<?>aashaImplementationNew(@Valid @RequestBody AashaImplementationTrust aashaImplementationTrust, @RequestParam(value = "requestId", required = false) String requestId)
    {
        try
        {
             AashaImplementationTrust aashaImplementationTrust1=calculationService.aashaImplementationTrust(aashaImplementationTrust);
            if(aashaImplementationTrust.getAmountProposedToBeReleased().compareTo(BigDecimal.ZERO) <0)
            {
                ResponseEntity.badRequest().body(new Error("Kindly check the entered data. The requested amount cannot be negative ","Kindly check the entered data. The requested amount cannot be negative"));
            }
            if(requestId!=null)
            {
                stateCordEdit stateCordEdit=new stateCordEdit();
                stateCordEdit.setAashaImplementationTrust(aashaImplementationTrust1);
              GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
                service.saveToCalcFlow(stateCordEdit,grantRequests);
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
    @GetMapping("/getVVSImpHybrid/{requestId}")
    public ResponseEntity<?>getVVShybridByRequestId(@PathVariable("requestId") String requestId)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            return ResponseEntity.ok().body(iVvsHybrid.findByRequestId(requestId));
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
    public ResponseEntity<?> getAashaHybrid(
            @PathVariable("requestId") String requestId,
            @RequestParam(value = "excel", required = false, defaultValue = "false") boolean excel) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();

            Optional<nha_grant_access.example.nha_grant.entity.AashaHybrid> optional = iAashaHybrid.findByRequestId(requestId);

            // ✅ Handle no data
            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found for requestId: " + requestId);
            }

            nha_grant_access.example.nha_grant.entity.AashaHybrid data = optional.get();

            // ✅ Excel case
            if (excel) {
                ByteArrayInputStream excelFile =
                        aashaHybridExcelService.generateExcel(List.of(data));

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=aasha_hybrid.xlsx")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(new InputStreamResource(excelFile));
            }

            // ✅ JSON case
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError()
                    .body(new Error("Error while fetching calculation details", e.toString()));
        }
    }
    @GetMapping("/getAashaAdm/{requestId}")
    public ResponseEntity<?> getAashaAdmin(
            @PathVariable("requestId") String requestId,
            @RequestParam(value = "excel", required = false, defaultValue = "false") boolean excel) {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();

            Optional<nha_grant_access.example.nha_grant.entity.AashaAdmin> optional = iAashaAdmin.findByRequestId(requestId);

            // ✅ No data case
            if (optional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No data found for requestId: " + requestId);
            }

            nha_grant_access.example.nha_grant.entity.AashaAdmin data = optional.get();

            // ✅ Excel case
            if (excel) {
                ByteArrayInputStream excelFile =
                        aashaAdminExcelService.generateExcel(List.of(data));

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=aasha_admin.xlsx")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(new InputStreamResource(excelFile));
            }

            // ✅ JSON
            return ResponseEntity.ok(data);

        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.internalServerError()
                    .body(new Error("Error while fetching calculation details", e.toString()));
        }
    }    @GetMapping("/getVvsAdm/{requestId}")
    public ResponseEntity<?>getVvsAdmin(@PathVariable("requestId") String requestId)
    {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String name = authentication.getName();
            return ResponseEntity.ok().body(vvsAdminRepo.findByRequestId(requestId));
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
