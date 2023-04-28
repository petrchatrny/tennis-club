package cz.petrchatrny.tennisclub.repository.court;

import cz.petrchatrny.tennisclub.model.Court;
import cz.petrchatrny.tennisclub.repository.ICRUDSoftDeleteRepository;

/**
 * Interface for court manipulation.
 *
 * @see ICRUDSoftDeleteRepository
 */
public interface ICourtRepository extends ICRUDSoftDeleteRepository<Court, Long> {
}
