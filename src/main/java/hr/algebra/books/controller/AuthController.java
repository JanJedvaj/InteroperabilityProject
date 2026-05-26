package hr.algebra.books.controller;

import hr.algebra.books.dto.AuthDTO;
import hr.algebra.books.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    // TODO: set to true in production (requires HTTPS)
    @Value("${app.cookie.secure:false}")
    private boolean secureCookie;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthDTO.LoginRequest request,
                                   HttpServletResponse response) {
        try {
            AuthDTO.TokenResponse result = authService.login(request);
            setRefreshCookie(response, result.getRefreshToken());
            return ResponseEntity.ok(new AuthDTO.LoginResponse(result.getAccessToken(), result.getRole()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDTO.LoginRequest request,
                                      HttpServletResponse response) {
        try {
            AuthDTO.TokenResponse result = authService.register(request);
            setRefreshCookie(response, result.getRefreshToken());
            return ResponseEntity.ok(new AuthDTO.LoginResponse(result.getAccessToken(), result.getRole()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        if (refreshToken == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Refresh token nedostaje"));
        }
        try {
            AuthDTO.TokenResponse result = authService.refresh(new AuthDTO.RefreshRequest(refreshToken));
            setRefreshCookie(response, result.getRefreshToken());
            return ResponseEntity.ok(new AuthDTO.LoginResponse(result.getAccessToken(), result.getRole()));
        } catch (Exception e) {
            clearRefreshCookie(response);
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response) {
        authService.logout(refreshToken);
        clearRefreshCookie(response);
        return ResponseEntity.ok(Map.of("message", "Logged out"));
    }

    private void setRefreshCookie(HttpServletResponse response, String rawToken) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", rawToken)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(refreshExpirationMs / 1000)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearRefreshCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Strict")
                .path("/api/auth")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
