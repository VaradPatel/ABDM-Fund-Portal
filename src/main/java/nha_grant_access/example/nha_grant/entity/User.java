package nha_grant_access.example.nha_grant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "mobile_number", nullable = false, unique = true)
    private String mobileNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime  createdAt;
    @Column(name = "updated_at")
    private LocalDateTime  updatedAt;

    @Column(name = "is_new")
    private Boolean isNew;
    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name="role_id")
    private Integer roleId;

    @Column(name = "approved_by")
    private Integer approvedBy;

    @Column(name= "designation")
    private String designation;

    @Column(name="is_activated")
    private Boolean isActivated;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserStateRole> userStateRoles = new HashSet<>();
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