package cz.petrchatrny.tennisclub.dto.court;

import cz.petrchatrny.tennisclub.dto.surface.ResponseSurfaceDTO;

/**
 * Response form of Court
 *
 * @param id      of court
 * @param surface of court
 */
public record ResponseCourtDTO(
        Long id,
        ResponseSurfaceDTO surface
) {
}