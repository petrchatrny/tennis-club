package cz.petrchatrny.tennisclub.service;

import cz.petrchatrny.tennisclub.model.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;

/**
 * Service used for authenticating users. It uses UserService to search for users
 * and JwtService to generate JWT tokens.
 *
 * @see UserService
 * @see JwtService
 */
@Service
public class AuthService {
    private final UserService userService;
    private final JwtService jwtService;

    /**
     * @param userService injected UserService
     * @param jwtService  injected JwtService
     */
    public AuthService(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    /**
     * Method checks HTTP header for basic authentication and if it finds it, it
     * parses credentials from header and tries to authenticate the user by
     * the obtained credentials. If user is authenticated, it generates JWT
     * token and puts it in the HTTP response's header.
     *
     * @param request HTTP request
     * @return HTTP response including status if user is authenticated or not
     */
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
