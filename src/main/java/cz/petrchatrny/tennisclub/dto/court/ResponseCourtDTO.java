package cz.petrchatrny.tennisclub.dto.court;

import cz.petrchatrny.tennisclub.dto.surface.ResponseSurfaceDTO;

public record ResponseCourtDTO(
        Long id,
        ResponseSurfaceDTO surface
) {
}