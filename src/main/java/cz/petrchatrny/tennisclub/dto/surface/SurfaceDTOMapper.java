package cz.petrchatrny.tennisclub.dto.surface;

import cz.petrchatrny.tennisclub.model.Surface;
import cz.petrchatrny.tennisclub.model.SurfacePrice;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

/**
 * Class containing mappers for conversion between Surface and SurfaceDTOs
 */
public class SurfaceDTOMapper {
    /**
     * Conversion of Surface model to ResponseSurfaceDTO
     *
     * @see Surface
     * @see ResponseSurfaceDTO
     */
    public static final Function<Surface, ResponseSurfaceDTO> SURFACE_TO_RESPONSE_SURFACE_DTO =
            surface -> {
                List<SurfacePrice> prices = surface.getPrices()
                        .stream()
                        .filter(price -> price.getValidTo() == null)
                        .toList();

                BigDecimal price = null;
                if (prices.size() > 0) {
                    price = prices.get(0).getPricePerMinuteInCzk();
                }

                return new ResponseSurfaceDTO(surface.getId(), price);
            };
}
