package cz.petrchatrny.tennisclub.service;

import cz.petrchatrny.tennisclub.dto.reservation.*;
import cz.petrchatrny.tennisclub.error.exception.ResourceAlreadyExistsException;
import cz.petrchatrny.tennisclub.error.exception.ResourceNotFoundException;
import cz.petrchatrny.tennisclub.error.exception.UnexpectedUserInputException;
import cz.petrchatrny.tennisclub.model.*;
import cz.petrchatrny.tennisclub.repository.reservation.IReservationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for manipulation with Reservation entity.
 *
 * <p>
 * It uses reservationRepository to perform CRUD operations and it also uses
 * UserService and CourtService for obtaining these entities from DB because
 * Reservation depends on Court and User.
 * </p>
 *
 * @see IReservationRepository
 * @see UserService
 * @see CourtService
 */
@Service
public class ReservationService {
    private final IReservationRepository reservationRepository;
    private final UserService userService;
    private final CourtService courtService;
    private final long MINIMAL_RESERVATION_DURATION_IN_MINUTES = 30;

    /**
     * @param reservationRepository injected repository
     * @param userService           injected service
     * @param courtService          injected service
     */
    public ReservationService(
            IReservationRepository reservationRepository,
            UserService userService,
            CourtService courtService
    ) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.courtService = courtService;
    }

    /**
     * Gets collection of all reservations that are not deleted.
     *
     * <p>
     * If any param is provided, collection will be filtered by provided param.
     * </p>
     *
     * @param phoneNumber user's phone number
     * @param courtNumber id of court
     * @param pendingOnly flag if only pending reservations should be included
     * @return collection of reservations as ResponseReservationDTO format
     */
    public Collection<ResponseReservationDTO> getReservations(
            String phoneNumber,
            Long courtNumber,
            Boolean pendingOnly
    ) {
        return reservationRepository
                .getAll(phoneNumber, courtNumber, pendingOnly)
                .stream()
                .map(ReservationDTOMapper.RESERVATION_TO_RESPONSE_RESERVATION_DTO)
                .peek(dto -> dto.setPrice(resolvePrice(dto.getId())))
                .toList();
    }


    /**
     * Creates new Reservation entity.
     *
     * <p>
     * Method checks user's input (meaningful time interval, etc), then it checks if
     * there is a court for which a reservation is made.
     * It also checks whether a user already exists in DB or if a new one should be created,
     * in which case it sends them a text message with their password.
     * </p>
     *
     * @param dto data transfer object containing all required fields
     * @return created reservation in ResponseReservationDTO format
     * @throws ResourceAlreadyExistsException if provided time interval is occupied
     */
    public ResponseReservationDTO createReservation(CreateReservationDTO dto) throws ResourceAlreadyExistsException {
        // validate inputs
        validateInputs(dto);

        // validate court
        Court court = courtService.checkExistingCourt(dto.getCourtNumber());

        // validate time interval
        if (reservationRepository.isIntervalConflicting(dto.getCourtNumber(), dto.getHeldAt(), dto.getHeldUntil())) {
            throw new ResourceAlreadyExistsException("the selected time interval is occupied", null);
        }

        // validate user
        User user = validateUser(dto);

        // create reservation
        Reservation reservation = ReservationDTOMapper.CREATE_RESERVATION_DTO_TO_RESERVATION.apply(dto);
        reservation.setCourt(court);
        reservation.setUser(user);
        reservationRepository.create(reservation);

        // return result
        ResponseReservationDTO res = ReservationDTOMapper.RESERVATION_TO_RESPONSE_RESERVATION_DTO.apply(reservation);
        res.setPrice(resolvePrice(res.getId()));
        return res;
    }

    /**
     * Updates existing Reservation entity.
     *
     * <p>
     * Searches for a reservation by id and if it finds it adjusts its values
     * </p>
     *
     * @param id  reservation id
     * @param dto data transfer object containing all required fields
     * @return updated reservation in ResponseReservationDTO format
     */
    public ResponseReservationDTO updateReservation(Long id, BaseReservationDTO dto) {
        // check reservation existence
        Reservation reservation = validateReservation(id);

        // validate inputs
        validateInputs(dto);

        // validate court
        Court court = courtService.checkExistingCourt(dto.getCourtNumber());

        // update reservation
        reservation.setCourt(court);
        reservation.setHeldAt(dto.getHeldAt());
        reservation.setHeldUntil(dto.getHeldUntil());
        reservation.setGameType(dto.getGameType());
        reservationRepository.update(reservation);

        // create response
        ResponseReservationDTO res = ReservationDTOMapper.RESERVATION_TO_RESPONSE_RESERVATION_DTO.apply(reservation);
        res.setPrice(resolvePrice(res.getId()));
        return res;
    }

    /**
     * Soft deletes Reservation entity.
     *
     * <p>
     * Searches for a reservation by id and if it finds it marks it as deleted.
     * </p>
     *
     * @param id reservation id
     */
    public void deleteReservation(Long id) {
        // check existence
        validateReservation(id);

        // delete reservation
        reservationRepository.delete(id);
    }

    private Reservation validateReservation(Long id) throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.getOne(id);
        if (reservation == null) {
            throw new ResourceNotFoundException("no reservation with given id");
        }

        return reservation;
    }

    private User validateUser(CreateReservationDTO dto) {
        User user;
        try {
            user = userService.createUser(dto.getPhoneNumber(), dto.getFullName());
        } catch (ResourceAlreadyExistsException ex) {
            // TODO check authorization
            user = userService.getUser(dto.getPhoneNumber());
        }

        return user;
    }

    private void validateInputs(BaseReservationDTO dto) throws UnexpectedUserInputException {
        Map<String, String> errors;

        if (dto instanceof CreateReservationDTO) {
            errors = createReservationInputValidation((CreateReservationDTO) dto);
        } else {
            errors = baseInputValidation(dto);
        }

        if (errors.size() > 0) {
            throw new UnexpectedUserInputException("could not create new reservation - invalid user input", errors);
        }
    }

    private Map<String, String> baseInputValidation(BaseReservationDTO dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto.getHeldAt() == null) {
            errors.put("heldAt", "is required field (ISO 8601)");
        }

        if (dto.getHeldUntil() == null) {
            errors.put("heldUntil", "is required field (ISO 8601)");
        }

        if (dto.getGameType() == null) {
            errors.put("gameType", "is required field (SINGLES, DOUBLES)");
        }

        if (dto.getCourtNumber() == null) {
            errors.put("courtNumber", "is required field");
        }

        if (dto.getHeldAt() != null && dto.getHeldUntil() != null) {
            long durationInMinutes = ChronoUnit.MINUTES.between(dto.getHeldAt(), dto.getHeldUntil());
            if (durationInMinutes < MINIMAL_RESERVATION_DURATION_IN_MINUTES) {
                errors.put("timeIntervalLength", "minimal duration is " + MINIMAL_RESERVATION_DURATION_IN_MINUTES + " minutes");
            }

            if (dto.getHeldAt().isBefore(LocalDateTime.now().plusDays(1L))) {
                errors.put("heldAt", "must be at least 1 day ahead from now");
            }
        }

        return errors;
    }

    private Map<String, String> createReservationInputValidation(CreateReservationDTO dto) {
        Map<String, String> errors = baseInputValidation(dto);

        if (dto.getPhoneNumber() == null) {
            errors.put("phoneNumber", "is required field");
        }

        if (dto.getFullName() == null) {
            errors.put("fullName", "is required field");
        }

        return errors;
    }

    private BigDecimal resolvePrice(Long id) {
        Reservation reservation = reservationRepository.getOne(id);
        BigDecimal price = reservationRepository.getPrice(id);

        if (reservation.getGameType() == GameType.DOUBLES) {
            price = price.multiply(BigDecimal.valueOf(1.5));
        }

        return price;
    }
}
