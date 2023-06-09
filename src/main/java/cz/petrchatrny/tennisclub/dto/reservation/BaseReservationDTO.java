package cz.petrchatrny.tennisclub.dto.reservation;

import cz.petrchatrny.tennisclub.model.GameType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Required fields for both create and update operations upon reservations.
 */
@Getter
@AllArgsConstructor
public class BaseReservationDTO {
    private final LocalDateTime heldAt;
    private final LocalDateTime heldUntil;
    private final Long courtNumber;
    private final GameType gameType;
}
