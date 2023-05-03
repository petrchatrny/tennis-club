package cz.petrchatrny.tennisclub.service;

import cz.petrchatrny.tennisclub.dto.court.CourtDTOMapper;
import cz.petrchatrny.tennisclub.dto.court.CreateCourtDTO;
import cz.petrchatrny.tennisclub.dto.court.ResponseCourtDTO;
import cz.petrchatrny.tennisclub.error.exception.ResourceNotFoundException;
import cz.petrchatrny.tennisclub.model.Court;
import cz.petrchatrny.tennisclub.model.Surface;
import cz.petrchatrny.tennisclub.repository.court.ICourtRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

/**
 * Service for manipulation with Courts.
 *
 * <p>
 * It uses courtRepository to perform CRUD operations upon Court Entity.
 * </p>
 *
 * @see ICourtRepository
 * @see SurfaceService
 */
@Service
public class CourtService {
    private final ICourtRepository courtRepository;
    private final SurfaceService surfaceService;

    /**
     * @param courtRepository injected repository
     * @param surfaceService  injected service
     */
    public CourtService(ICourtRepository courtRepository, SurfaceService surfaceService) {
        this.courtRepository = courtRepository;
        this.surfaceService = surfaceService;
    }

    /**
     * Gets collection of all Courts that are not deleted.
     *
     * @return collection of courts as ResponseCourtDTO
     */
    public Collection<ResponseCourtDTO> getCourts() {
        return this.courtRepository
                .getAll()
                .stream()
                .map(CourtDTOMapper.COURT_TO_RESPONSE_COURT_DTO)
                .toList();
    }

    /**
     * Creates new Court.
     *
     * <p>
     * Checks that the specified surface exists and, if so,
     * creates a new court
     * </p>
     *
     * @param dto input in CreateCourtDTO format
     * @return created Court in ResponseCourtDTO format
     */
    public ResponseCourtDTO createCourt(CreateCourtDTO dto) {
        // check existence of surface
        Surface surface = surfaceService.checkExistingSurface(dto.id_surface());

        // create court
        Court court = courtRepository.create(
                Court.builder()
                        .surface(surface)
                        .build()
        );

        // return response
        return CourtDTOMapper.COURT_TO_RESPONSE_COURT_DTO.apply(court);
    }

    /**
     * Updates Court.
     *
     * <p>
     * Searches for a court by id and if it finds it, it checks that the specified surface exists.
     * If so, it updates Court.
     * </p>
     *
     * @param id  court id
     * @param dto data transfer object containing all required fields
     * @return updated court in ResponseCourtDTO format
     */
    public ResponseCourtDTO updateCourt(Long id, CreateCourtDTO dto) {
        // check existence of court
        Court court = checkExistingCourt(id);

        // check existence of surface
        Surface surface = surfaceService.checkExistingSurface(dto.id_surface());

        // update court
        court.setSurface(surface);
        court = courtRepository.update(court);

        return CourtDTOMapper.COURT_TO_RESPONSE_COURT_DTO.apply(court);
    }

    /**
     * Soft deletes Court entity.
     *
     * <p>
     * Searches for a court by id and if it finds marks it as deleted.
     * </p>
     *
     * @param id court id
     */
    public void deleteCourt(Long id) {
        // check existence of Court
        checkExistingCourt(id);

        // delete court
        courtRepository.delete(id);
    }

    /**
     * Searches for a court by id and if it finds it returns it.
     * Otherwise, it throws ResourceNotFoundException.
     *
     * @param id court id
     * @return court
     * @throws ResourceNotFoundException if there is no court with provided id
     */
    public Court checkExistingCourt(Long id) throws ResourceNotFoundException {
        Court court = courtRepository.getOne(id);
        if (court == null) {
            throw new ResourceNotFoundException("no court with given id");
        }

        return court;
    }
}
