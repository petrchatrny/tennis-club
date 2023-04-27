package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class User extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @NotNull
    private String fullName;

    @NotNull
    @Column(unique = true)
    private String phoneNumber;

    @NotNull
    private String password;

    @NotNull
    private String salt;

    @NotNull
    private UserRole role;

    @OneToMany(mappedBy = "user")
    private Set<Reservation> reservations;
}
