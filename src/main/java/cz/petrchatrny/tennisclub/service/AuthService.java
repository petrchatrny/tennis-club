package cz.petrchatrny.tennisclub.service;

import cz.petrchatrny.tennisclub.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;

@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;

    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    public ResponseEntity<?> authenticate(HttpServletRequest request) {
        // check header
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // parse credentials
        String[] credentials = parseCredentials(authorizationHeader);
        String phoneNumber = credentials[0];
        String password = credentials[1];

        // authenticate user
        User user = userService.authenticateUser(phoneNumber, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // generate JWT
        String token = jwtService.generateToken(new HashMap<>(), phoneNumber);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        return ResponseEntity.ok().headers(headers).build();
    }

    private String[] parseCredentials(String authorizationHeader) {
        String base64Credentials = authorizationHeader.substring(6);
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        return credentials.split(":");
    }
}
