package abdm_nha_grant_access.example.nha_grant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "fund_breakup_report",
        uniqueConstraints = @UniqueConstraint(columnNames = {"report_date", "state_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FundBreakupReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "report_date", nullable = false)
    private LocalDate reportDate;

    @Column(name = "financial_year", nullable = false)
    private String financialYear;

    @Column(name = "quarter", nullable = false)
    private String quarter;

    @Column(name = "state_id", nullable = false)
    private Integer stateId;

    @Column(name = "state_name", nullable = false)
    private String stateName;

    @Column(name = "total_fund_released", nullable = false)
    private BigDecimal totalFundReleased;

    @Column(name = "total_expenditure", nullable = false)
    private BigDecimal totalExpenditure;

    @Column(name = "total_balance", nullable = false)
    private BigDecimal totalBalance;

    @Column(name = "hr_fund_released", nullable = false)
    private BigDecimal hrFundReleased;

    @Column(name = "hr_expenditure", nullable = false)
    private BigDecimal hrExpenditure;

    @Column(name = "hr_balance", nullable = false)
    private BigDecimal hrBalance;

    @Column(name = "iec_cb_fund_released", nullable = false)
    private BigDecimal iecCbFundReleased;

    @Column(name = "iec_cb_expenditure", nullable = false)
    private BigDecimal iecCbExpenditure;

    @Column(name = "iec_cb_balance", nullable = false)
    private BigDecimal iecCbBalance;

    @Column(name = "admin_fund_released", nullable = false)
    private BigDecimal adminFundReleased;

    @Column(name = "admin_expenditure", nullable = false)
    private BigDecimal adminExpenditure;

    @Column(name = "admin_balance", nullable = false)
    private BigDecimal adminBalance;

    @Column(name = "updated_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

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
