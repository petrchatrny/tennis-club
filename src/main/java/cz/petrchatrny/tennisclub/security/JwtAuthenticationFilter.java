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

        // get JWT
        jwt = authHeader.substring(AUTHENTICATION_SCHEME.length());
        userPhoneNumber = jwtService.getUserPhoneNumber(jwt);

        // check JWT
        if (jwtService.isTokenExpired(jwt) || userService.getUser(userPhoneNumber) == null) {
            return;
        }

        // check user
        if (userPhoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            User user = userService.getUser(userPhoneNumber);
            if (user != null) {
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(user.getRole().toString()));

                SecurityContextHolder.getContext().setAuthentication(
                        new UsernamePasswordAuthenticationToken(
                                userPhoneNumber, null, authorities
                        )
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}
