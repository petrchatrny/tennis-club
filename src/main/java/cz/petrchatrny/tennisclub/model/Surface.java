package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

/**
 * Entity representing surface of tennis court
 */
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

    /**
     * @return list of valid prices
     */
    public List<SurfacePrice> getValidPrices() {
        return getPrices()
                .stream()
                .filter(price -> price.getValidTo() == null)
                .toList();
    }
}
