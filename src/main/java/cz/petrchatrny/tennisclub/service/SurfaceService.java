package cz.petrchatrny.tennisclub.service;

import cz.petrchatrny.tennisclub.dto.court.CourtDTOMapper;
import cz.petrchatrny.tennisclub.dto.surface.CreateSurfaceDTO;
import cz.petrchatrny.tennisclub.dto.surface.ResponseSurfaceDTO;
import cz.petrchatrny.tennisclub.dto.surface.SurfaceDTOMapper;
import cz.petrchatrny.tennisclub.error.exception.ResourceDependencyException;
import cz.petrchatrny.tennisclub.error.exception.ResourceNotFoundException;
import cz.petrchatrny.tennisclub.error.exception.UnexpectedUserInputException;
import cz.petrchatrny.tennisclub.model.Court;
import cz.petrchatrny.tennisclub.model.Surface;
import cz.petrchatrny.tennisclub.model.SurfacePrice;
import cz.petrchatrny.tennisclub.repository.court.ICourtRepository;
import cz.petrchatrny.tennisclub.repository.surface.ISurfaceRepository;
import cz.petrchatrny.tennisclub.repository.surfaceprice.ISurfacePriceRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Service for manipulation with Surface entity.
 *
 * <p>
 * It uses repositories to perform CRUD operations upon Surface and SurfacePrice entities.
 * </p>
 *
 * @see ISurfaceRepository
 * @see ISurfacePriceRepository
 * @see ICourtRepository
 */
@Service
public final class SurfaceService {
    private final ISurfaceRepository surfaceRepository;
    private final ISurfacePriceRepository surfacePriceRepository;
    private final ICourtRepository courtRepository;

    /**
     * @param surfaceRepository      injected repository
     * @param surfacePriceRepository injected repository
     * @param courtRepository        injected repository
     */
    public SurfaceService(
            ISurfaceRepository surfaceRepository,
            ISurfacePriceRepository surfacePriceRepository,
            ICourtRepository courtRepository
    ) {
        this.surfaceRepository = surfaceRepository;
        this.surfacePriceRepository = surfacePriceRepository;
        this.courtRepository = courtRepository;
    }

    /**
     * Gets collection of all Surfaces that are not deleted.
     *
     * @return collection of surfaces as ResponseSurfaceDTO
     */
    public Collection<ResponseSurfaceDTO> getSurfaces() {
        return surfaceRepository.getAll()
                .stream()
                .map(SurfaceDTOMapper.SURFACE_TO_RESPONSE_SURFACE_DTO)
                .toList();
    }

    /**
     * Creates new Surface and also new SurfacePrice.
     *
     * @param dto input in CreateSurfaceDTO format
     * @return created Surface in ResponseSurfaceDTO format
     */
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

    /**
     * Searches for a surface by id and if it finds it, checks if new price is provided.
     * If yes new SurfacePrice is created and the old is marked as old (invalid).
     *
     * @param id  surface id
     * @param dto data transfer object containing all required fields
     * @return updated surface in ResponseSurfaceDTO format
     */
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

    /**
     * Soft deletes Surface entity.
     *
     * <p>
     * Searches for a surface by id and if it finds it checks if Surface is not
     * a dependency for some Court. If not, it marks it as deleted and invalidate
     * all its remaining valid SurfacePrices.
     * </p>
     *
     * @param id surface id
     */
    public void deleteSurface(Long id) {
        // check surface existence
        Surface surface = checkExistingSurface(id);
        surface.getPrices().forEach(price -> surfacePriceRepository.invalidate(price.getId()));

        // check surface dependency
        checkSurfaceDependencies(surface.getId());

        // delete surface
        surfaceRepository.delete(id);
    }

    /**
     * Searches for a surface by id and if it finds it returns it.
     * Otherwise, it throws ResourceNotFoundException.
     *
     * @param id surface id
     * @return surface
     * @throws ResourceNotFoundException if there is no surface with provided id
     */
    public Surface checkExistingSurface(Long id) throws ResourceNotFoundException {
        Surface surface = surfaceRepository.getOne(id);
        if (surface == null) {
            throw new ResourceNotFoundException("no surface with given id");
        }

        return surface;
    }

    private void validateInputs(CreateSurfaceDTO dto) throws UnexpectedUserInputException {
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
            errors.put("pricePerMinuteInCzk", "should be greater than or equal to 0");
        }

        return errors;
    }

    private SurfacePrice createNewPrice(BigDecimal pricePerMinuteInCzk) {
        return surfacePriceRepository.create(
                SurfacePrice.builder()
                        .validFrom(LocalDateTime.now())
                        .pricePerMinuteInCzk(pricePerMinuteInCzk)
                        .build()
        );
    }

    private void checkSurfaceDependencies(Long surfaceId) throws ResourceDependencyException {
        Collection<Court> dependentCourts = courtRepository.getAllBySurface(surfaceId);
        if (!dependentCourts.isEmpty()) {
            throw new ResourceDependencyException(
                    "could not delete surface - it is dependency for courts",
                    dependentCourts.stream().map(CourtDTOMapper.COURT_TO_RESPONSE_COURT_DTO).toList());
        }
    }
}
