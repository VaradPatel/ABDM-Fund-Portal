package nha_grant_access.example.nha_grant.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_state_role")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserStateRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "state_id", nullable = false)
    private States state;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonIgnore
    private Roles role;

    @Column(name = "designation")
    private String designation;
}
