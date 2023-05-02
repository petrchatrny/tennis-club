package cz.petrchatrny.tennisclub.dto.reservation;

import cz.petrchatrny.tennisclub.model.GameType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
public final class ResponseReservationDTO {
    private final Long id;
    private final LocalDateTime heldAt;
    private final LocalDateTime heldUntil;
    private final Long courtNumber;
    private final GameType gameType;
    private  ResponseUserDTO user;
    private  BigDecimal price;
}
