package cz.petrchatrny.tennisclub.dto.reservation;

import cz.petrchatrny.tennisclub.model.Reservation;
import cz.petrchatrny.tennisclub.model.User;

import java.util.function.Function;

/**
 * Class containing mappers for conversion between Reservation and ReservationDTOs
 */
public class ReservationDTOMapper {
    /**
     * Conversion of Reservation model to ResponseReservationDTO
     *
     * @see Reservation
     * @see ResponseReservationDTO
     */
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

    /**
     * Conversion of CreateReservationDTO to Reservation model.
     *
     * @see Reservation
     * @see CreateReservationDTO
     */
    public static final Function<CreateReservationDTO, Reservation> CREATE_RESERVATION_DTO_TO_RESERVATION =
            dto -> Reservation.builder()
                    .heldAt(dto.getHeldAt())
                    .heldUntil(dto.getHeldUntil())
                    .gameType(dto.getGameType())
                    .build();

    /**
     * Conversion of User model to ResponseUserDTO.
     *
     * @see User
     * @see ResponseUserDTO
     */
    public static final Function<User, ResponseUserDTO> USER_TO_RESPONSE_USER_DTO = user ->
            new ResponseUserDTO(user.getPhoneNumber(), user.getFullName());
}
