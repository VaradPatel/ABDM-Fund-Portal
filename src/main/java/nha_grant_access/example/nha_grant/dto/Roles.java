package nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;


import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Roles {
    @NotNull(message = "RoleId is mandatory")
    private Integer id;
    @NotNull(message = "Rolename is mandatory")
    private String name;
}


