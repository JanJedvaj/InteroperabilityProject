package hr.algebra.books.auth.service;

import hr.algebra.books.auth.dto.LoginRequest;
import hr.algebra.books.auth.dto.RefreshRequest;
import hr.algebra.books.auth.dto.TokenPair;
import hr.algebra.books.security.jwt.JwtService;
import hr.algebra.books.user.domain.AppUser;
import hr.algebra.books.user.repository.AppUserRepository;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public TokenPair login(LoginRequest req) {
        AppUser user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }
        return new TokenPair(
                jwtService.issueAccessToken(user.getUsername()),
                jwtService.issueRefreshToken(user.getUsername())
        );
    }

    public TokenPair refresh(RefreshRequest req) {
        String token = req.refreshToken();
        if (!jwtService.isValid(token) || jwtService.isAccessToken(token)) {
            throw new BadCredentialsException("Invalid refresh token");
        }
        String username = jwtService.extractUsername(token);
        return new TokenPair(
                jwtService.issueAccessToken(username),
                jwtService.issueRefreshToken(username)
        );
    }
}
