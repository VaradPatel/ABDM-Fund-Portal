package nha_grant_access.example.nha_grant.service;
import nha_grant_access.example.nha_grant.entity.Roles;
import nha_grant_access.example.nha_grant.entity.User;
import nha_grant_access.example.nha_grant.repository.IRolesRepository;
import nha_grant_access.example.nha_grant.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetails implements UserDetailsService {
    @Autowired
    UserRepo userRepositiry;
    @Autowired
    IRolesRepository iRolesRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepositiry.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Roles roles=

                iRolesRepository.findById(user.getRoleId())
                        .orElseThrow(() -> new RuntimeException("Role not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(roles.getName())

                .build();


    }
}
