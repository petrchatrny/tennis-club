package cz.petrchatrny.tennisclub.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing price of court's surface.
 * It allows to keep track of price changes and get the correct prices in the past.
 */
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

