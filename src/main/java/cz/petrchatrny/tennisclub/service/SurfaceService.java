package cz.petrchatrny.tennisclub.service;

import cz.petrchatrny.tennisclub.dto.surface.CreateSurfaceDTO;
import cz.petrchatrny.tennisclub.dto.surface.ResponseSurfaceDTO;
import cz.petrchatrny.tennisclub.dto.surface.SurfaceDTOMapper;
import cz.petrchatrny.tennisclub.error.exception.ResourceNotFoundException;
import cz.petrchatrny.tennisclub.error.exception.UnexpectedUserInputException;
import cz.petrchatrny.tennisclub.model.Surface;
import cz.petrchatrny.tennisclub.model.SurfacePrice;
import cz.petrchatrny.tennisclub.repository.surface.ISurfaceRepository;
import cz.petrchatrny.tennisclub.repository.surfaceprice.ISurfacePriceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
public final class SurfaceService {
    private final ISurfaceRepository surfaceRepository;
    private final ISurfacePriceRepository surfacePriceRepository;

    public SurfaceService(ISurfaceRepository surfaceRepository, ISurfacePriceRepository surfacePriceRepository) {
        this.surfaceRepository = surfaceRepository;
        this.surfacePriceRepository = surfacePriceRepository;
    }

    public Collection<ResponseSurfaceDTO> getSurfaces() {
        return surfaceRepository.getAll()
                .stream()
                .map(SurfaceDTOMapper.SURFACE_TO_RESPONSE_SURFACE_DTO)
                .toList();
    }

    public ResponseSurfaceDTO createSurface(CreateSurfaceDTO dto) {
        validateInputs(dto);

        // create price
        SurfacePrice price = createNewPrice(dto.pricePerMinuteInCzk());

        // create surface
        Surface surface = Surface.builder()
                .prices(new HashSet<>())
                .build();
        surface.getPrices().add(price);
        surface = surfaceRepository.create(surface);

        // return result
        return SurfaceDTOMapper.SURFACE_TO_RESPONSE_SURFACE_DTO.apply(surface);
    }

    public ResponseSurfaceDTO updateSurface(Long id, CreateSurfaceDTO dto) {
        // check existing
        Surface surface = checkExistingSurface(id);

        // validate inputs
        validateInputs(dto);

        // check price change
        SurfacePrice price;
        if (surface.getValidPrices().size() != 0) {
            price = surface.getValidPrices().get(0);
            if (!price.getPricePerMinuteInCzk().equals(dto.pricePerMinuteInCzk())) {
                surfacePriceRepository.invalidate(price.getId());
            }
        }
        price = createNewPrice(dto.pricePerMinuteInCzk());
        surface.getPrices().add(price);

        // return result
        surface = surfaceRepository.update(surface);
        return SurfaceDTOMapper.SURFACE_TO_RESPONSE_SURFACE_DTO.apply(surface);
    }

    public void deleteSurface(Long id) {
        Surface surface = checkExistingSurface(id);
        surface.getPrices().forEach(price -> surfacePriceRepository.invalidate(price.getId()));

        surfaceRepository.delete(id);
    }

    private void validateInputs(CreateSurfaceDTO dto) {
        Map<String, String> errors = validation(dto);
        if (errors.size() > 0) {
            throw new UnexpectedUserInputException("could not create new surface - invalid user input", errors);
        }
    }

    private Map<String, String> validation(CreateSurfaceDTO dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto.pricePerMinuteInCzk() == null) {
            // null price handling
            errors.put("pricePerMinuteInCzk", "is required field");
        } else if (dto.pricePerMinuteInCzk().compareTo(BigDecimal.ZERO) < 0) {
            // negative price handling
            errors.put("pricePerMinuteInCzk", "should be bigger than 0");
        }

        return errors;
    }

    public Surface checkExistingSurface(Long id) {
        Surface surface = surfaceRepository.getOne(id);
        if (surface == null) {
            throw new ResourceNotFoundException("no surface with given id");
        }

        return surface;
    }

    private SurfacePrice createNewPrice(BigDecimal pricePerMinuteInCzk) {
        return surfacePriceRepository.create(
                SurfacePrice.builder()
                        .validFrom(LocalDateTime.now())
                        .pricePerMinuteInCzk(pricePerMinuteInCzk)
                        .build()
        );
    }
}
