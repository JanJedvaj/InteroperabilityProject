package hr.algebra.books.service;

import hr.algebra.books.dto.AuthDTO;
import hr.algebra.books.model.User;
import hr.algebra.books.repository.UserRepository;
import hr.algebra.books.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthDTO.TokenResponse login(AuthDTO.LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Pogrešna lozinka");
        }

        String accessToken = jwtService.generateAccessToken(
            user.getUsername(), user.getRole()
        );
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new AuthDTO.TokenResponse(accessToken, refreshToken, user.getRole());
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
        String refreshToken = jwtService.generateRefreshToken(user.getUsername());
        return new AuthDTO.TokenResponse(accessToken, refreshToken, user.getRole());
    }

    public AuthDTO.TokenResponse refresh(AuthDTO.RefreshRequest request) {
        if (!jwtService.isTokenValid(request.getRefreshToken())) {
            throw new RuntimeException("Refresh token nije validan");
        }

        String username = jwtService.extractUsername(request.getRefreshToken());
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Korisnik nije pronađen"));

        String newAccessToken = jwtService.generateAccessToken(
            user.getUsername(), user.getRole()
        );
        String newRefreshToken = jwtService.generateRefreshToken(user.getUsername());

        return new AuthDTO.TokenResponse(newAccessToken, newRefreshToken, user.getRole());
    }
}
