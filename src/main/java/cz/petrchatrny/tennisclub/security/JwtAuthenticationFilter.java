package cz.petrchatrny.tennisclub.security;

import cz.petrchatrny.tennisclub.model.User;
import cz.petrchatrny.tennisclub.service.JwtService;
import cz.petrchatrny.tennisclub.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used for authentication control using JWT tokens.
 * For requests to protected endpoints it checks the header to see if
 * it contains a valid JWT token and authenticates the user if it does.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final String AUTHENTICATION_SCHEME = "Bearer ";
    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userPhoneNumber;

        // check header is correct
        if (authHeader == null || !authHeader.startsWith(AUTHENTICATION_SCHEME)) {
            filterChain.doFilter(request, response);
            return;
        }

        // get data
        jwt = authHeader.substring(AUTHENTICATION_SCHEME.length());
        userPhoneNumber = jwtService.getUserPhoneNumber(jwt);
        User user = userService.getUser(userPhoneNumber);

        // check JWT
        if (jwtService.isTokenExpired(jwt) || user == null) {
            filterChain.doFilter(request, response);
            return;
        }

        // make user authenticated and authorized
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(
                            userPhoneNumber, null, authorities
                    )
            );
        }

        filterChain.doFilter(request, response);
    }
}
