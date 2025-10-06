package nha_grant_access.example.nha_grant.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

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

    @Column(name = "aasha_imp_max_elg_grant")
    private BigDecimal aashaImpMaxElgGrant;

    @Column(name = "aasha_admin_max_elg_grant")
    private BigDecimal aashaAdminMaxElgGrant;

    @Column(name = "vvs_imp_max_elg_grant")
    private BigDecimal vvsImpMaxElgGrant;

    @Column(name = "vvs_admin_max_elg_grant")
    private BigDecimal vvsAdminMaxElgGrant;


    private BigDecimal q1;
    private BigDecimal q2;
    private BigDecimal q3;
    private BigDecimal q4;

    @Column(name = "tranche_distribution", columnDefinition = "jsonb") // JSONB for PostgreSQL
    @JdbcTypeCode(SqlTypes.JSON) // Hibernate 6+ annotation for JSON support
    private List<BigDecimal> trancheDistribution;
}
