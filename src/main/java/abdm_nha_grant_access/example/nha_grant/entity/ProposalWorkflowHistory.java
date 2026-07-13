package abdm_nha_grant_access.example.nha_grant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "proposal_workflow_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProposalWorkflowHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "proposal_id", nullable = false)
    private Integer proposalId;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    // Stores WorkflowAction.name() - see abdm_nha_grant_access.example.nha_grant.enums.WorkflowAction
    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "remarks")
    private String remarks;

    // Stores ProposalStatus.getId() as of right after this action was applied
    @Column(name = "status_after", nullable = false)
    private Integer statusAfter;

    @Column(name = "created_at", columnDefinition = "timestamp default CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
