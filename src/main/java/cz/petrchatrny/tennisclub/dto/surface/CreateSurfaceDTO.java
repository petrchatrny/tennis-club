package cz.petrchatrny.tennisclub.dto.surface;

import java.math.BigDecimal;

/**
 * Required fields for creating Surface
 *
 * @param pricePerMinuteInCzk of surface
 */
public record CreateSurfaceDTO(BigDecimal pricePerMinuteInCzk) {
}
