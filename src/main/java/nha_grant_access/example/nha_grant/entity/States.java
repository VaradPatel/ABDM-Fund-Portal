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

    @Column(name = "mode_id")
    private Integer modeId;

    @Column(name = "tcs_dashboard_state_id")
    private Integer tcsDashboardStateId;

    @Column(name = "tcs_pmjay_family")
    private Integer tcsPmjayFamily;

    @Column(name = "tcs_new_family")
    private Integer tcsNewFamily;

    @Column(name = "tcs_old_family")
    private Integer tcsOldFamily;

    @Column(name = "tcs_aasha_family")
    private Integer tcsAashaFamily;



    private BigDecimal q1;
    private BigDecimal q2;
    private BigDecimal q3;
    private BigDecimal q4;

    @Column(name = "tranche_distribution", columnDefinition = "jsonb") // JSONB for PostgreSQL
    @JdbcTypeCode(SqlTypes.JSON) // Hibernate 6+ annotation for JSON support
    private List<BigDecimal> trancheDistribution;

    @Column(name = "insurance_company")
    private String insuranceCompany;
    
    @Column(name = "premium_amount")
    private BigDecimal premiumAmount;
}
