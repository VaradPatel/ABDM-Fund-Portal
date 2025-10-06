package nha_grant_access.example.nha_grant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMaxElgGrants {
    private Integer stateId;
    private Integer schemeId;
    private Integer proposalTypeId;
    private BigDecimal MaxElgGrant;
}
