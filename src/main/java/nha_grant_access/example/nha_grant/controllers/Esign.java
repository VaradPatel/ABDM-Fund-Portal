package nha_grant_access.example.nha_grant.controllers;

import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.Esign.*;
import nha_grant_access.example.nha_grant.entity.GrantRequests;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.repository.IGrantRequestsRepo;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import nha_grant_access.example.nha_grant.service.DelayedFollowUpService;
import nha_grant_access.example.nha_grant.service.EsignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/esign")
@Slf4j
public class Esign {
    @Autowired
    EsignService esignService;
    @Autowired
    UserRepo userRepo;
    @Autowired
    DelayedFollowUpService delayedFollowUpService;
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;
    @PostMapping("/generate-request/{userId}/{requestId}")
    @PreAuthorize("hasAuthority('State CEO') ")
    public ResponseEntity<?> generateESignRequest(@RequestBody DocumentRequest documentRequest, @PathVariable Integer userId ,@PathVariable String requestId){
        try {
            User user=userRepo.findById(userId).get();
            GrantRequests grantRequests=iGrantRequestsRepo.findGrantRequestByRequestId(requestId);
            if(grantRequests.getESignStatusStateCeo())
            {
                return ResponseEntity.badRequest().body(new Error ("Already Esigned","Already Esigned"));
            }
            MatchAadharDetailsTO matchAadharDetailsTO=new MatchAadharDetailsTO(user.getDob().toString().substring(0, 4),user.getName(),user.getGender(),"NHAGRANTS");
documentRequest.getDocument().setMatchAadharDetailsTO(matchAadharDetailsTO);

documentRequest.getDocument().setEmailId(user.getEmail());
documentRequest.getDocument().setStateCEOName(user.getName());
documentRequest.getDocument().setMobileNumber(user.getMobileNumber());
documentRequest.getDocument().setSigningPlace(documentRequest.getDocument().getState());

            System.out.println("document is "+ documentRequest.toString());

            EspResponse response = esignService.sendToDigiSignApi(documentRequest);


            delayedFollowUpService.fetchSignedPdfOnce(response.getAspTxnId(), requestId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Error("Error while calling DigiSign API: "  ,  e.getMessage() ));
        }
    }
@GetMapping("/status/{requestId}")
@PreAuthorize("hasAuthority('State CEO') ")
    public ResponseEntity<?> CheckEsignStatus(@PathVariable("requestId") String requestId)
{
    try {
        GrantRequests grantRequests=iGrantRequestsRepo.findByRequestId(requestId).get();
        StatusResponse statusResponse=new StatusResponse();
        statusResponse.setEsign_status(grantRequests.getESignStatusStateCeo());
        statusResponse.setEsign_certificate(grantRequests.getEsign_pdf());
       return ResponseEntity.ok().body(statusResponse);

    }
    catch (Exception e)
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Error("Error while checking ESign Status: "  ,  e.getMessage() ));

    }
}



}
