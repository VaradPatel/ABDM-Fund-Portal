package abdm_nha_grant_access.example.nha_grant.controllers;

import abdm_nha_grant_access.example.nha_grant.entity.*;
import abdm_nha_grant_access.example.nha_grant.repository.*;
import abdm_nha_grant_access.example.nha_grant.dto.Error;
import abdm_nha_grant_access.example.nha_grant.utils.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;

import java.util.List;

@RestController
@CrossOrigin
public class stateController {
    @Autowired
    IstatesRepository istatesRepository;
    @Autowired
    IRolesRepository iRolesRepository;


    @Autowired
    RSAUtil rsaUtil;
    @Autowired
    UserRepo userRepo;

    @GetMapping(path = "/all-states", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<States> states() {

        return istatesRepository.findAll();

    }

    @GetMapping(path = "/state/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<States> stateById(@PathVariable Integer id) {

        return istatesRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping(path = "/all-roles", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Roles> roles() throws Exception {

        return iRolesRepository.findAll();

    }


}


