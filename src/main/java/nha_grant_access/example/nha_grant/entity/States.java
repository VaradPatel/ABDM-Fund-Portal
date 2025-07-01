package nha_grant_access.example.nha_grant.entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.BigInteger;

@Entity
@Table(name = "states")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class States {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String name;
    private BigDecimal stateShareRatio;
    private BigDecimal nhaShareRatio;
    private BigInteger beneficCount;
    private BigDecimal implementationMaxEligibleGrant;
    private BigDecimal ceilAmount;
    private BigDecimal nhaShare;
    private BigDecimal administrativeMaxEligibleGrant;
}
