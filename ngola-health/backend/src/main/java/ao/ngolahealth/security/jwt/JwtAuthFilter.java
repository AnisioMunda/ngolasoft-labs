package ao.ngolahealth.security.jwt;

import ao.ngolahealth.modules.auth.service.TokenBlackListService;
import ao.ngolahealth.security.tenant.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService             jwtService;
    private final UserDetailsService     userDetailsService;
    private final ObjectMapper           objectMapper;
    private final TokenBlackListService  blacklistService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest  request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);

            if (blacklistService.isBlacklisted(jwt)) {
                log.warn("Access attempt with revoked token");
                handleException(response, HttpStatus.FORBIDDEN,
                    "Token revoked. Please login again.");
                return;
            }

            final String username = jwtService.extractUsername(jwt);

            if (username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {
                authenticateInternalUser(jwt, username, request);
            }

            filterChain.doFilter(request, response);

        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: {}", e.getMessage());
            handleException(response, HttpStatus.UNAUTHORIZED,
                "Token expired. Please login again.");
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token: {}", e.getMessage());
            handleException(response, HttpStatus.UNAUTHORIZED, "Invalid token.");
        } catch (JwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
            handleException(response, HttpStatus.UNAUTHORIZED, "Invalid token.");
        } catch (UsernameNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            handleException(response, HttpStatus.UNAUTHORIZED, "User not found.");
        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            handleException(response, HttpStatus.UNAUTHORIZED, "Authentication error.");
        } finally {
            TenantContext.clear();
        }
    }

    // ----------------------------------------------------------------
    // Utilizador interno — comportamento original sem alterações
    // ----------------------------------------------------------------

    private void authenticateInternalUser(
        String jwt, String username, HttpServletRequest request) {

        UserDetails userDetails =
            userDetailsService.loadUserByUsername(username);

        if (jwtService.isTokenValid(jwt, userDetails)) {
            UUID hospitalId = jwtService.extractHospitalId(jwt);
            if (hospitalId != null) {
                TenantContext.setCurrentHospital(hospitalId);
            }

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authToken);

            log.debug("Interno autenticado: {} (hospital: {})",
                username, hospitalId);
        }
    }

    // ----------------------------------------------------------------
    // shouldNotFilter — igual ao original
    // ----------------------------------------------------------------

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/auth/")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/api-docs")
            || path.startsWith("/swagger-resources")
            || path.startsWith("/webjars");
    }

    // ----------------------------------------------------------------
    // handleException — igual ao original
    // ----------------------------------------------------------------

    private void handleException(HttpServletResponse response,
        HttpStatus status, String message) throws IOException {

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now().toString());
        error.put("status",    status.value());
        error.put("error",     status.getReasonPhrase());
        error.put("message",   message);

        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}