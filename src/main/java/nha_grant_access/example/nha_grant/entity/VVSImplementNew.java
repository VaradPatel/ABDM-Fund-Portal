package nha_grant_access.example.nha_grant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "vvs_implementtrust_calc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VVSImplementNew {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;


    @Column(name = "request_id")
    private String requestId;
    @Column(name="roleId")
    private Integer  roleId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    @Column(name = "new_beneficiary_in_state")
    private BigDecimal newBeneficiaryInstate;

    @Column(name="old_beneficiary_in_state")
    private BigDecimal oldBeneficiaryInstate;

    @Column(name = "max_gia_implementation_per_family_new")
    private BigDecimal maxGiaImplementationPerFamilyNew;

    @Column(name = "max_gia_implementation_per_family_old")
    private BigDecimal maxGiaImplementationPerFamilyOld;

    @Column(name = "max_gia_implementation_by_nha")
    private BigDecimal maxGiaImplementationByNha;

    @Column(name = "policy_period")
    private String policyPeriod;

    // Section 2 - Financial Details

    @Column(name = "total_treatment_cost_paid_by_sha")
    private BigDecimal totalTreatmentCostPaidBySha;

    @Column(name = "nha_share_in_pmjay_treatment_cost")
    private BigDecimal nhaShareInPmjayTreatmentCost;

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
        this.createdAt = LocalDateTime.now(); // Set only once
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();

    }
}
