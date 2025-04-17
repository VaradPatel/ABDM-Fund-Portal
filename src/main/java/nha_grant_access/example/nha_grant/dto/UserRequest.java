package nha_grant_access.example.nha_grant.dto;

import lombok.*;
import nha_grant_access.example.nha_grant.entity.Roles;
import nha_grant_access.example.nha_grant.entity.States;

import javax.management.relation.Role;
import java.util.Date;

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
   private String name;
   private Date createdAt;
   private Boolean isVerified;
   private Boolean isActivated;
}
