package cz.petrchatrny.tennisclub.repository.reservation;

import cz.petrchatrny.tennisclub.model.Reservation;
import cz.petrchatrny.tennisclub.repository.ICRUDSoftDeleteRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Interface for reservations manipulation.
 *
 * @see ICRUDSoftDeleteRepository
 */
public interface IReservationRepository extends ICRUDSoftDeleteRepository<Reservation, Long> {
    /**
     * Gets all reservations or filter them by provided parameters
     * @param userPhoneNumber user's unique phone number
     * @param courtId id of tennis court
     * @param pendingOnly flag if only pending reservations should be returned
     * @return collection of reservations
     */
    Collection<Reservation> getAll(String userPhoneNumber, Long courtId, Boolean pendingOnly);

    /**
     * Checks if new reservation in provided court during the provided time is possible.
     * @param courtId id of court where reservations should be done
     * @param heldAt start of time interval
     * @param heldUntil end of time interval
     * @return boolean if provided interval produces conflict or not
     */
    Boolean isIntervalConflicting(Long courtId, LocalDateTime heldAt, LocalDateTime heldUntil);

    /**
     * Gets price of reservation.
     * @param id of reservation
     * @return price
     */
    BigDecimal getPrice(Long id);
}
