package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "surface_prices")
public class SurfacePrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_surface_price")
    private Long id;

    @NotNull
    private LocalDateTime validFrom;

    private LocalDateTime validTo;

    @NotNull
    private BigDecimal pricePerMinuteInCzk;
}

