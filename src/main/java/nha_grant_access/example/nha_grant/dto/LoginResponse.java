package nha_grant_access.example.nha_grant.dto;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nha_grant_access.example.nha_grant.entity.Roles;
import nha_grant_access.example.nha_grant.entity.States;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponse {

    private Integer userId;
    private String email;
    private String mobile;
    private String name;
    private Roles role;
    private boolean isNew;
    private List<States> state;
    private String token;


}
