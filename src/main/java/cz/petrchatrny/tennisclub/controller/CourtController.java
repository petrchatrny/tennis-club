package cz.petrchatrny.tennisclub.controller;

import cz.petrchatrny.tennisclub.config.OpenApiConfig;
import cz.petrchatrny.tennisclub.dto.court.CreateCourtDTO;
import cz.petrchatrny.tennisclub.dto.court.ResponseCourtDTO;
import cz.petrchatrny.tennisclub.service.CourtService;
import cz.petrchatrny.tennisclub.util.AdminEndpoint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Tag(name = "courts")
@RestController
@RequestMapping("/api/v1/courts")
public class CourtController {
    private final CourtService courtService;

    public CourtController(CourtService courtService) {
        this.courtService = courtService;
    }

    @GetMapping("/")
    @Operation(summary = "Get list of all courts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = {
                    @Content(
                            array = @ArraySchema(schema = @Schema(implementation = ResponseCourtDTO.class)),
                            mediaType = "application/json"
                    )
            }),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content)
    })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME)
    public ResponseEntity<Collection<ResponseCourtDTO>> getCourts() {
        return new ResponseEntity<>(courtService.getCourts(), HttpStatus.OK);
    }

    @PostMapping("/")
    @Operation(summary = "Create new court")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Court created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Surface not found", content = @Content)
    })
    @AdminEndpoint
    public ResponseEntity<ResponseCourtDTO> createCourt(@RequestBody CreateCourtDTO dto) {
        return new ResponseEntity<>(courtService.createCourt(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update court")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Court updated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Court not found / Surface not found", content = @Content),
    })
    @AdminEndpoint
    public ResponseEntity<ResponseCourtDTO> updateCourt(@PathVariable Long id, @RequestBody CreateCourtDTO dto) {
        return new ResponseEntity<>(courtService.updateCourt(id, dto), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete court")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Court deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Court not found")
    })
    @AdminEndpoint
    public ResponseEntity<Void> deleteCourt(@PathVariable Long id) {
        courtService.deleteCourt(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
