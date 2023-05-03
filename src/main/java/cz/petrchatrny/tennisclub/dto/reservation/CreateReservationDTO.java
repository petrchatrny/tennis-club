package cz.petrchatrny.tennisclub.dto.reservation;

import cz.petrchatrny.tennisclub.model.GameType;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Required fields for creating Reservation
 */
@Getter
public class CreateReservationDTO extends BaseReservationDTO{
    private final  String phoneNumber;
    private final String fullName;

    public CreateReservationDTO(LocalDateTime heldAt, LocalDateTime heldUntil, Long courtNumber, GameType gameType, String phoneNumber, String fullName) {
        super(heldAt, heldUntil, courtNumber, gameType);
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
    }
}