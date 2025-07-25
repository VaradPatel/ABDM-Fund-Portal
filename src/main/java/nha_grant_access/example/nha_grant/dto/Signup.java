package nha_grant_access.example.nha_grant.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import nha_grant_access.example.nha_grant.entity.Roles;

import java.time.LocalDate;
import java.util.List;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Signup {

    @NotNull(message="State is mandatory") // ✅ Use @NotNull for non-string fields
    private List<Integer> stateId;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Mobile number is mandatory")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number format")
    private String mobile;

    @NotBlank(message = "Name is mandatory")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only letters and spaces")
    private String name;

    @NotNull(message = "Roles are mandatory") // ✅ Use @NotNull for objects
    private Roles roles;

    @NotBlank(message = "Designation is mandatory")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Name must contain only letters and spaces")
    private String designation;

    @NotBlank(message="gender is compulsory")

    private String gender;

    @NotNull(message = "dob is mandatory")
    private LocalDate dob;

    @NotBlank(message = "transaction Id cannot be null")
    private String transactionId;

}
