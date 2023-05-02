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

@Service
public class CourtService {
    private final ICourtRepository courtRepository;
    private final SurfaceService surfaceService;

    public CourtService(ICourtRepository courtRepository, SurfaceService surfaceService) {
        this.courtRepository = courtRepository;
        this.surfaceService = surfaceService;
    }

    public Collection<ResponseCourtDTO> getCourts() {
        return this.courtRepository
                .getAll()
                .stream()
                .map(CourtDTOMapper.COURT_TO_RESPONSE_COURT_DTO)
                .toList();
    }

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

    public void deleteCourt(Long id) {
        // check existence of Court
        checkExistingCourt(id);

        // delete court
        courtRepository.delete(id);
    }

    public Court checkExistingCourt(Long id) {
        Court court = courtRepository.getOne(id);
        if (court == null) {
            throw new ResourceNotFoundException("no court with given id");
        }

        return court;
    }
}
