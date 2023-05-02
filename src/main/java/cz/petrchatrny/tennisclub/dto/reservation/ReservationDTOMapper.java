package cz.petrchatrny.tennisclub.dto.reservation;

import cz.petrchatrny.tennisclub.model.Reservation;
import cz.petrchatrny.tennisclub.model.User;

import java.util.function.Function;

public class ReservationDTOMapper {
    public static final Function<Reservation, ResponseReservationDTO> RESERVATION_TO_RESPONSE_RESERVATION_DTO =
            reservation -> {
                ResponseUserDTO user = ReservationDTOMapper.USER_TO_RESPONSE_USER_DTO.apply(reservation.getUser());
                return new ResponseReservationDTO(
                        reservation.getId(),
                        reservation.getHeldAt(),
                        reservation.getHeldUntil(),
                        reservation.getCourt().getId(),
                        reservation.getGameType(),
                        reservation.getCreatedAt(),
                        user,
                        null
                );
            };

    public static final Function<CreateReservationDTO, Reservation> CREATE_RESERVATION_DTO_TO_RESERVATION =
            dto -> Reservation.builder()
                    .heldAt(dto.getHeldAt())
                    .heldUntil(dto.getHeldUntil())
                    .gameType(dto.getGameType())
                    .build();

    public static final Function<User, ResponseUserDTO> USER_TO_RESPONSE_USER_DTO = user ->
            new ResponseUserDTO(user.getPhoneNumber(), user.getFullName());
}
