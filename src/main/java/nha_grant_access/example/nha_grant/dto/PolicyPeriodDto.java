package nha_grant_access.example.nha_grant.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PolicyPeriodDto {


    private Timestamp policyStartDate;
        private Timestamp  policyEndDate;
        private BigDecimal amountReleasedTillDate;




    }

