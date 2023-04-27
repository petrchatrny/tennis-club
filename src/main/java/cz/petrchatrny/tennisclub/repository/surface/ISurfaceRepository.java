package cz.petrchatrny.tennisclub.repository.surface;

import cz.petrchatrny.tennisclub.model.Surface;
import cz.petrchatrny.tennisclub.repository.ICRUDSoftDeleteRepository;

/**
 * Interface for  surfaces manipulation.
 *
 * @see ICRUDSoftDeleteRepository
 */
public interface ISurfaceRepository extends ICRUDSoftDeleteRepository<Surface, Long> {
}
