package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "reservations")
public class Reservation extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reservation")
    private Long id;

    @NotNull
    private LocalDateTime heldAt;

    @NotNull
    private LocalDateTime heldUntil;

    @NotNull
    private GameType gameType;

    @ManyToOne
    @JoinColumn(name = "id_court", nullable = false)
    private Court court;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;
}
