package nha_grant_access.example.nha_grant.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PolicyPeriodDto {


    private Date policyStartDate;
        private Date policyEndDate;



    }

