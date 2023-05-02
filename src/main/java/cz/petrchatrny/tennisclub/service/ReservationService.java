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

@Service
public class ReservationService {
    private final IReservationRepository reservationRepository;
    private final UserService userService;
    private final CourtService courtService;
    private final long MINIMAL_RESERVATION_DURATION_IN_MINUTES = 30;

    public ReservationService(
            IReservationRepository reservationRepository,
            UserService userService,
            CourtService courtService
    ) {
        this.reservationRepository = reservationRepository;
        this.userService = userService;
        this.courtService = courtService;
    }

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

    public ResponseReservationDTO createReservation(CreateReservationDTO dto) {
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

    public void deleteReservation(Long id) {
        // check existence
        validateReservation(id);

        // delete reservation
        reservationRepository.delete(id);
    }

    private Reservation validateReservation(Long id) {
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

    private void validateInputs(BaseReservationDTO dto) {
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
