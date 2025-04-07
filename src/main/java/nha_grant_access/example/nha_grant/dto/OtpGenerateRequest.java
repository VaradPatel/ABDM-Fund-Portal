package nha_grant_access.example.nha_grant.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigInteger;
@Data
@AllArgsConstructor
@NoArgsConstructor

public class OtpGenerateRequest {
    @NotBlank(message = "mobile  number cannot be null")
    private String mobile;
    private String type;


















}
