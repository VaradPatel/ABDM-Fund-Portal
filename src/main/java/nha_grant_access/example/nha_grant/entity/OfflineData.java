package nha_grant_access.example.nha_grant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nha_grant_access.example.nha_grant.dto.StateShare;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "offline_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfflineData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



        @ManyToOne
        @JoinColumn(name = "state_id", nullable = false)
        private States state;



        @ManyToOne
        @JoinColumn(name = "proposal_type_id", nullable = false)
        private ProposalType proposalType;

        @ManyToOne
        @JoinColumn(name = "implementation_mode_id", nullable = false)
        private ImplementationTypes implementationMode;


        @Column(name = "policy_start_date", nullable = false)
        private LocalDate policyStartDate;

        @Column(name = "policy_end_date", nullable = false)
        private LocalDate policyEndDate;

        @Column(name = "financial_year", nullable = false, length = 9)
        private String financialYear;

        //    @Column(name = "tranche", nullable = false)
//    private String tranche;
        @Column(name = "tranche", columnDefinition = "jsonb") // JSONB for PostgreSQL
        @JdbcTypeCode(SqlTypes.JSON) // Hibernate 6+ annotation for JSON support
        private List<Integer> tranche;






    @Column(name = "requested_date")
    private LocalDate requestedDate;

    @Column(name = "released_date")
    private LocalDate releasedDate;


        @Column(name = "requested_amount", nullable = false)
        private BigDecimal requestedAmount;

        @Column(name = "released_amount")
        private BigDecimal releasedAmount;

        //    @Column(name = "state_share", nullable = false)
//    private BigDecimal stateShare;



        @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
        private LocalDateTime createdAt;




        @Column(name = "updated_at",  columnDefinition = "timestamp default CURRENT_TIMESTAMP")
        private LocalDateTime updatedAt;






        @Column(name="sc_amount")
        private BigDecimal amountSc;

        @Column(name="st_amount")
        private BigDecimal amountSt;

        @Column(name="gc_amount")
        private BigDecimal amountGc;



        @Column(name="scheme_id")
        private Integer schemeId;

        @Column(name="scheme_name")
        private String schemeName;

        @Column(name="approved")
        private Integer approved;






        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now(); // Set only once
            this.updatedAt = LocalDateTime.now();
        }
        @PreUpdate
        protected void onUpdate() {
            this.updatedAt = LocalDateTime.now(); // Only updated_at changes
        }
    }


