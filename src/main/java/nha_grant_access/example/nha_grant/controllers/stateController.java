package nha_grant_access.example.nha_grant.controllers;

import com.google.common.util.concurrent.CycleDetectingLockFactory;
import nha_grant_access.example.nha_grant.dto.Error;
import nha_grant_access.example.nha_grant.dto.PolicyPeriodDto;
import nha_grant_access.example.nha_grant.entity.*;
import nha_grant_access.example.nha_grant.repository.*;
import nha_grant_access.example.nha_grant.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class stateController {
    @Autowired
    IstatesRepository istatesRepository;
    @Autowired
    IRolesRepository iRolesRepository;
    @Autowired
    ImplementationTypesRepo implementationTypesRepo;
    @Autowired
    IProposalTypesRepo iProposalTypesRepo;
    @Autowired
    IGrantRequestsRepo iGrantRequestsRepo;

    @Autowired
    RSAUtil rsaUtil;
    @Autowired
    UserRepo userRepo;

    @GetMapping(path = "/all-states", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<States> states() {

        return istatesRepository.findAll();

    }
    @GetMapping(path = "/all-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Roles> roles() throws Exception {

        return iRolesRepository.findAll();

    }
    @GetMapping(path = "/all-implementation", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ImplementationTypes> implementationTypes() {
        return implementationTypesRepo.findAll();

    }
    @GetMapping(path = "/all-proposal", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ProposalType> proposalTypes() {
        return iProposalTypesRepo.findAll();

    }
    @GetMapping(path="/policy-period", produces=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?>getPolicyPeriodByStateId(@RequestParam Integer stateId)
    {
        try {
            List<Integer>stateIds;
            List<Object[]> results;
            if(stateId==0)
            {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                String name = authentication.getName();
                User user = userRepo.findByEmail(name).get();

                if(user.getRoleId()>3)
                {
                    results=iGrantRequestsRepo.findAllDistinctPolicyPeriods();
                }
                else {
                    stateIds = userRepo.findStateIdByRole(user.getRoleId(), user.getId());
                    results=iGrantRequestsRepo.findDistinctPolicyPeriodsByStateId(stateIds);
                }
            }
            else {
                results = iGrantRequestsRepo.findDistinctPolicyPeriodsByStateId(Collections.singletonList(stateId));
            }
            List<PolicyPeriodDto> result = results.stream()
                    .map(obj -> new PolicyPeriodDto((Timestamp) obj[0], (Timestamp) obj[1],(BigDecimal) obj[2]))
                    .toList();
            return ResponseEntity.ok().body(result);
        }
        catch (Exception e)
        {
            return ResponseEntity.internalServerError().body(new Error("error while fetching policy period",e.toString()));
        }
    }


}
