package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing tennis court
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "courts")
public class Court extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_court")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_surface", nullable = false)
    private Surface surface;
}
