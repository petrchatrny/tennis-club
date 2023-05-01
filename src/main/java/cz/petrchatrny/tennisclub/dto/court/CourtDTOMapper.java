package cz.petrchatrny.tennisclub.dto.court;

import cz.petrchatrny.tennisclub.dto.surface.SurfaceDTOMapper;
import cz.petrchatrny.tennisclub.model.Court;

import java.util.function.Function;

public class CourtDTOMapper {
    public static final Function<Court, ResponseCourtDTO> COURT_TO_RESPONSE_COURT_DTO = court -> new ResponseCourtDTO(
            court.getId(),
            SurfaceDTOMapper.SURFACE_TO_RESPONSE_SURFACE_DTO.apply(court.getSurface())
    );
}
