package nha_grant_access.example.nha_grant.controllers;

import nha_grant_access.example.nha_grant.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
//    @PostMapping("/login")
//    public ResponseEntity<?> createGrantRequest(@RequestBody @Valid GrantRequestInputDto grantRequestInputDTO) {
//
//
private final AuthenticationManager authenticationManager;
@Autowired
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public LoginController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
public ResponseEntity<?> login() {
        try {
//
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken("varadrpatel@gmail.com","password")
//            );
//
//            UserDetails user = (UserDetails) authentication.getPrincipal();

            UserDetails user= userDetailsService.loadUserByUsername("varadrpatel@gmail.com");
            String jwtToken = jwtUtil.generateToken("varadrpatel@gmail.com", 1,"9096182522","varadrpatel@gmail.com");

            return ResponseEntity.ok(jwtToken + " " + user.getUsername() +" "+ user.getPassword());
        }

        catch (UsernameNotFoundException e){
            return ResponseEntity.status(401).body("Invalid username");
        }
        catch(Exception e)
        {
            return ResponseEntity.internalServerError().body(e.toString());
        }
}

}
