package cz.petrchatrny.tennisclub.controller;

import cz.petrchatrny.tennisclub.config.OpenApiConfig;
import cz.petrchatrny.tennisclub.dto.surface.CreateSurfaceDTO;
import cz.petrchatrny.tennisclub.dto.surface.ResponseSurfaceDTO;
import cz.petrchatrny.tennisclub.service.SurfaceService;
import cz.petrchatrny.tennisclub.util.AdminEndpoint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Tag(name = "surfaces")
@RestController
@RequestMapping("/api/v1/surfaces")
public class SurfaceController {
    private final SurfaceService surfaceService;

    public SurfaceController(SurfaceService surfaceService) {
        this.surfaceService = surfaceService;
    }

    @GetMapping("/")
    @Operation(summary = "Get list of surfaces")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content)
    })
    @SecurityRequirement(name = OpenApiConfig.SECURITY_SCHEME)
    public ResponseEntity<Collection<ResponseSurfaceDTO>> getSurfaces() {
        return new ResponseEntity<>(surfaceService.getSurfaces(), HttpStatus.OK);
    }

    @PostMapping("/")
    @Operation(summary = "Create new surface")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Surface created", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content)
    })
    @AdminEndpoint
    public ResponseEntity<ResponseSurfaceDTO> createCourt(@RequestBody CreateSurfaceDTO dto) {
        return new ResponseEntity<>(surfaceService.createSurface(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update surface")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "202", description = "Surface updated", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access", content = @Content),
            @ApiResponse(responseCode = "403", description = "Unauthorized access", content = @Content),
            @ApiResponse(responseCode = "404", description = "Surface not found", content = @Content)
    })
    @AdminEndpoint
    public ResponseEntity<ResponseSurfaceDTO> updateCourt(@PathVariable Long id, @RequestBody CreateSurfaceDTO dto) {
        return new ResponseEntity<>(surfaceService.updateSurface(id, dto), HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete surface")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Surface deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthenticated access"),
            @ApiResponse(responseCode = "403", description = "Unauthorized access"),
            @ApiResponse(responseCode = "404", description = "Surface not found"),
            @ApiResponse(responseCode = "409", description = "Surface is dependency for a Court")
    })
    @AdminEndpoint
    public ResponseEntity<Void> deleteCourt(@PathVariable Long id) {
        surfaceService.deleteSurface(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}