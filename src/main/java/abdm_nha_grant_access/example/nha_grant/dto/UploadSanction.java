package abdm_nha_grant_access.example.nha_grant.dto;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadSanction {


private String requestId;
        private BigDecimal amountSC;
        private BigDecimal amountST;
        private BigDecimal amountGC;


        private LocalDateTime sanctionDate;
  //  @NotBlank
    private String sanctionLetter;

    public byte[] getSanctionLetterBytes() {
        return Base64.getDecoder().decode(sanctionLetter);
    }


    }

