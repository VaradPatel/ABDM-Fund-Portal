package abdm_nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.A;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePassword {
    private String transactionId;
    @NotNull(message = "mobile is mandatory")
    private String mobile;
    @NotNull(message = "password is manadatory")
    private String password;
    private Boolean isNew;

}
