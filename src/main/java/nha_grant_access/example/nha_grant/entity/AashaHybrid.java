package nha_grant_access.example.nha_grant.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "asha_hybrid_calc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AashaHybrid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Section 1 - Implementation Details
    @Column(name = "state_name")
    private String stateName;

    @Column(name = "mode_of_implementation")
    private String modeOfImplementation;

    @Column(name = "date_of_implementation")
    private String dateOfImplementation;

    @Column(name = "benefit_cover")
    private BigDecimal benefitCover;

    @Column(name = "nha_share_in_gia")
    private BigDecimal nhaShareInGia;

    @Column(name = "scheme_name")
    private String schemeName;

    @Column(name = "total_families_covered_in_state_as_per_mou")
    private BigDecimal totalFamiliesCoveredInStateAsPerMou;

    @Column(name = "total_eligible_asha_aww_awh_families")
    private BigDecimal totalEligibleAshaAwwAwhFamilies;

    @Column(name = "max_gia_implementation_by_nha_per_family")
    private BigDecimal maxGiaImplementationByNhaPerFamily;

    @Column(name = "max_gia_implementation_by_nha")
    private BigDecimal maxGiaImplementationByNha;

    @Column(name = "policy_period")
    private String policyPeriod;

    // Section 2 - Financial Details
    @Column(name = "name_of_insurance_company")
    private String nameOfInsuranceCompany;

    @Column(name = "annual_insurance_premium_family")
    private BigDecimal annualInsurancePremiumFamily;

    @Column(name = "nha_share_of_premium_payable")
    private BigDecimal nhaShareOfPremiumPayable;

    @Column(name = "sha_share_of_premium_payable")
    private BigDecimal shaShareOfPremiumPayable;

    @Column(name = "upfront_release_by_sha_for_pmjay")
    private BigDecimal upfrontReleaseByShaForPmjay;

    @Column(name = "nha_share_corresponding_to_sha_release")
    private BigDecimal nhaShareCorrespondingToShaRelease;

    @Column(name = "payment_tranche_no")
    private BigDecimal paymentTrancheNo;

    @Column(name = "total_amount_payable_till_this_tranche")
    private BigDecimal totalAmountPayableTillThisTranche;

    @Column(name = "total_amount_payable_by_nha_as_on_date")
    private BigDecimal totalAmountPayableByNhaAsOnDate;

    @Column(name = "earlier_amount_released_by_nha")
    private BigDecimal earlierAmountReleasedByNha;

    @Column(name = "unspent_amount_as_per_uc")
    private BigDecimal unspentAmountAsPerUc;

    @Column(name = "amount_proposed_to_be_released")
    private BigDecimal amountProposedToBeReleased;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}


