package nha_grant_access.example.nha_grant.controllers;

import nha_grant_access.example.nha_grant.entity.States;
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
    @GetMapping(path = "/states", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<States> states() {
        return istatesRepository.findAll();

    }


}
