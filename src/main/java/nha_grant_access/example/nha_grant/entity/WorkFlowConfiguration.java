package nha_grant_access.example.nha_grant.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Table(name = "work_flow_configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkFlowConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "action_performed", nullable = false)
    private Action actionPerformed;

    @ManyToOne
    @JoinColumn(name = "action_performed_by")
    private Roles actionPerformedBy;

    @ManyToOne
    @JoinColumn(name = "assign_to")
    private Roles assignTo;

    @ManyToOne
    @JoinColumn(name = "flow_status", referencedColumnName = "id")
    private StatusDescription statusDescription;

}