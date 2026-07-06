package abdm_nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class VerifyOtpRequest {

    @NotBlank
    String transactionId;

    @NotEmpty(message = "Contact cannot be null")
    String contact;



    @NotEmpty(message = "Otp cannot be null")
    String otp;

    Boolean islogin;
}
