package cz.petrchatrny.tennisclub.dto.surface;

import java.math.BigDecimal;

/**
 * Response form of Surface
 *
 * @param id                  of surface
 * @param pricePerMinuteInCzk of surface
 */
public record ResponseSurfaceDTO(
        Long id,
        BigDecimal pricePerMinuteInCzk
) {
}
