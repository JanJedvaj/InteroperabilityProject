package hr.algebra.books.service;

import hr.algebra.books.dto.AuthDTO;
import hr.algebra.books.model.RefreshToken;
import hr.algebra.books.model.User;
import hr.algebra.books.repository.RefreshTokenRepository;
import hr.algebra.books.repository.UserRepository;
import hr.algebra.books.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpirationMs;

    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Pogrešna lozinka");
        }

        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        String rawRefreshToken = jwtService.generateRefreshToken(user.getUsername());
        persistRefreshToken(rawRefreshToken, user.getUsername());

        return new AuthDTO.TokenResponse(accessToken, rawRefreshToken, user.getRole());
    }

    public AuthDTO.TokenResponse register(AuthDTO.LoginRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Korisnik već postoji");
        }
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("READ_ONLY")
                .build();
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        String rawRefreshToken = jwtService.generateRefreshToken(user.getUsername());
        persistRefreshToken(rawRefreshToken, user.getUsername());

        return new AuthDTO.TokenResponse(accessToken, rawRefreshToken, user.getRole());
    }

    @Transactional
    public AuthDTO.TokenResponse refresh(AuthDTO.RefreshRequest request) {
        String incomingHash = hash(request.getRefreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHash(incomingHash)
                .orElseThrow(() -> new RuntimeException("Refresh token nije validan"));

        if (stored.isRevoked()) {
            // Reuse of an already-used token → possible theft; nuke entire session
            refreshTokenRepository.revokeAllByUsername(stored.getUsername());
            throw new RuntimeException("Refresh token je već bio korišten — sumnja na krađu. Prijavite se ponovo.");
        }

        if (Instant.now().isAfter(stored.getExpiresAt())) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new RuntimeException("Refresh token je istekao");
        }

        // Validate JWT signature/expiry as a secondary check
        if (!jwtService.isTokenValid(request.getRefreshToken())) {
            stored.setRevoked(true);
            refreshTokenRepository.save(stored);
            throw new RuntimeException("Refresh token nije validan");
        }

        // Rotate: revoke old, issue new
        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        String username = stored.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        String newAccessToken = jwtService.generateAccessToken(user.getUsername(), user.getRole());
        String newRawRefreshToken = jwtService.generateRefreshToken(user.getUsername());
        persistRefreshToken(newRawRefreshToken, user.getUsername());

        return new AuthDTO.TokenResponse(newAccessToken, newRawRefreshToken, user.getRole());
    }

    @Transactional
    public void logout(String rawRefreshToken) {
        if (rawRefreshToken == null || rawRefreshToken.isBlank()) return;
        String incomingHash = hash(rawRefreshToken);
        refreshTokenRepository.findByTokenHash(incomingHash).ifPresent(token -> {
            if (!token.isRevoked()) {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            }
        });
    }

    private void persistRefreshToken(String rawToken, String username) {
        RefreshToken entity = RefreshToken.builder()
                .tokenHash(hash(rawToken))
                .username(username)
                .createdAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(refreshExpirationMs))
                .revoked(false)
                .build();
        refreshTokenRepository.save(entity);
    }

    private String hash(String raw) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encoded = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encoded);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
