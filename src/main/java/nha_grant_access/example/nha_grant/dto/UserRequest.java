package nha_grant_access.example.nha_grant.dto;

import lombok.*;
import nha_grant_access.example.nha_grant.entity.Roles;
import nha_grant_access.example.nha_grant.entity.States;

import javax.management.relation.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

    private Integer id;
    private String email;
    private String mobile;
    private String designation;
   private String stateName;
   private String roleName;
}
