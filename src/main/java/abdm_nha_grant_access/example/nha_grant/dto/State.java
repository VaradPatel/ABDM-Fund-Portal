package abdm_nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class State {
    @NotNull(message = "stateId is mandatory")
    private Integer id;
    @NotNull(message = "statename is mandatory")
    private String name;
}

