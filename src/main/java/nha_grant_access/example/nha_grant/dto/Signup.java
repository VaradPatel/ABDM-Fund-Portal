package nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import nha_grant_access.example.nha_grant.entity.Roles;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signup {

    @NotNull(message="state is mandatory")
    private List<Integer> stateId;

    @NotNull(message = "email is mandatory")
    private String email;

    @NotNull(message = "mobile is mandatory")
    private String mobile;

    @NotNull(message = "name is mandatory")
    private String name;

    @NotNull(message = "roles is mandatory")
    private Roles roles;

    @NotNull(message = "designation is mandatory")
    private String designation;

}
