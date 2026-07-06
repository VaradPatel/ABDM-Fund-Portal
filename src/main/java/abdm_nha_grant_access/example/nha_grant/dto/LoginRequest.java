package abdm_nha_grant_access.example.nha_grant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginRequest {
    @NotBlank(message = "email is mandatory")
    @JsonProperty("email")
    private String email;
    @NotBlank(message = "password is mandatory")
    @JsonProperty("password")
    private String password;


}
