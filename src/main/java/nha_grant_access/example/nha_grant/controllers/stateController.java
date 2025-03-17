package nha_grant_access.example.nha_grant.controllers;

import nha_grant_access.example.nha_grant.entity.ImplementationTypes;
import nha_grant_access.example.nha_grant.entity.ProposalType;
import nha_grant_access.example.nha_grant.entity.Roles;
import nha_grant_access.example.nha_grant.entity.States;
import nha_grant_access.example.nha_grant.repository.IProposalTypesRepo;
import nha_grant_access.example.nha_grant.repository.IRolesRepository;
import nha_grant_access.example.nha_grant.repository.ImplementationTypesRepo;
import nha_grant_access.example.nha_grant.repository.IstatesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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

    @GetMapping(path = "/all-states", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<States> states() {
        return istatesRepository.findAll();

    }
    @GetMapping(path = "/all-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Roles> roles() {
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


}
