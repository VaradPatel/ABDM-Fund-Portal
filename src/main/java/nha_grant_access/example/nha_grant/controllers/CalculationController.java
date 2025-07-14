package nha_grant_access.example.nha_grant.controllers;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.ImplementationNhaPayementDetails;
import nha_grant_access.example.nha_grant.service.CalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/calc")
@Slf4j
public class CalculationController {
    @Autowired
    CalculationService calculationService;
    @PostMapping("/implementation")
    public ResponseEntity<?>Implementation(@Valid @RequestBody ImplementationNhaPayementDetails implementationNhaPayementDetails)
    {
        try
        {
ImplementationNhaPayementDetails implementationNhaPayementDetails1=calculationService.implementationCalc(implementationNhaPayementDetails);
return ResponseEntity.ok().body(implementationNhaPayementDetails1);

        }
        catch(Exception e)
        {
            log.error(e.toString());
          return   ResponseEntity.internalServerError().body(new Error("Error while calculation", e.toString()));
        }
    }
}
