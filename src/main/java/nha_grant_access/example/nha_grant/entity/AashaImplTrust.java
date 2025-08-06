package nha_grant_access.example.nha_grant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asha_impl_trust")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AashaImplTrust {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // Section 1 - Implementation Details
    private String stateName;  // UT Name
    private String modeOfImplementation;
    private String dateOfImplementation;
    private BigDecimal benefitCover;
    private BigDecimal nhaShareInGia;
    private String schemeName;
    private BigDecimal totalFamiliesCoveredInStateAsPerMou;
    private BigDecimal totalEligibleAshaAwwAwhFamilies;
    private BigDecimal maxGiaImplementationByNhaPerFamily;
    private BigDecimal maxGiaImplementationByNha;
    private String policyPeriod;

    // Section 2 - Financial Details
    private BigDecimal totalTreatmentCostPaidBySha;
    private BigDecimal nhaShareInCostForAshaAwsAww;
    private BigDecimal upfrontReleaseByShaForPmjay;
    private BigDecimal nhaShareCorrespondingToShaRelease;
    private BigDecimal paymentTrancheNo;
    private BigDecimal totalAmountPayableTillThisTranche;
    private BigDecimal totalAmountPayableByNhaAsOnDate;
    private BigDecimal earlierAmountReleasedByNha;
    private BigDecimal unspentAmountAsPerUc;
    private BigDecimal amountProposedToBeReleased;

    // Metadata
    private Integer roleId;
    private String requestId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
