package cz.petrchatrny.tennisclub.dto.surface;

import java.math.BigDecimal;

public record ResponseSurfaceDTO(
        Long id,
        BigDecimal pricePerMinuteInCzk
) {
}
