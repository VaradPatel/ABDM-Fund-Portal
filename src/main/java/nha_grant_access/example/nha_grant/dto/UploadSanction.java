package nha_grant_access.example.nha_grant.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadSanction {


private String requestId;
        private BigDecimal amountSC;
        private BigDecimal amountST;
        private BigDecimal amountGC;

        private LocalDateTime sanctionDate; // You can change to LocalDate if parsing needed
    }

