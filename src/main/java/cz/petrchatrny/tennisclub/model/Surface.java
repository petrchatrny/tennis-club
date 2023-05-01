package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "surfaces")
public class Surface extends SoftDeletableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_surface")
    private Long id;

    @OneToMany
    @JoinColumn(name = "id_surface")
    private Set<SurfacePrice> prices;

    public List<SurfacePrice> getValidPrices() {
        return getPrices()
                .stream()
                .filter(price -> price.getValidTo() == null)
                .toList();
    }
}
