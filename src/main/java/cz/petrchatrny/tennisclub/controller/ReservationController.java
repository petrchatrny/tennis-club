package cz.petrchatrny.tennisclub.controller;

import cz.petrchatrny.tennisclub.dto.reservation.BaseReservationDTO;
import cz.petrchatrny.tennisclub.dto.reservation.CreateReservationDTO;
import cz.petrchatrny.tennisclub.dto.reservation.ResponseReservationDTO;
import cz.petrchatrny.tennisclub.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Tag(name = "reservations")
@RestController
@RequestMapping("/api/v1/reservations")
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/")
    @Operation(summary = "Get list of reservations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content)
    })
    public ResponseEntity<Collection<ResponseReservationDTO>> getReservations(
            @RequestParam(required = false) String phoneNumber,
            @RequestParam(required = false) Long courtNumber,
            @RequestParam(required = false) Boolean pendingOnly
    ) {
        return new ResponseEntity<>(reservationService.getReservations(phoneNumber, courtNumber, pendingOnly), HttpStatus.OK);
    }

    @PostMapping("/")
    @Operation(summary = "Create new reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Reservation created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "Court not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Time interval occupied", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unexpected user input", content = @Content)
    })
    public ResponseEntity<ResponseReservationDTO> createReservation(@RequestBody CreateReservationDTO dto) {
        return new ResponseEntity<>(reservationService.createReservation(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Reservation updated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Reservation not found / Court not found", content = @Content),
            @ApiResponse(responseCode = "409", description = "Time interval occupied", content = @Content),
            @ApiResponse(responseCode = "422", description = "Unexpected user input", content = @Content)
    })
    public ResponseEntity<ResponseReservationDTO> updateReservation(@PathVariable Long id, @RequestBody BaseReservationDTO dto) {
        return new ResponseEntity<>(reservationService.updateReservation(id, dto), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reservation")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Reservation deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Reservation not found")
    })
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservation(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
