package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "courts")
public class Court extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_court")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_surface", nullable = false)
    private Surface surface;
}
