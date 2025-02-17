package org.example.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.controlleradvice.Errors;
import org.example.dto.request.LoginUserDTO;
import org.example.dto.request.SignUpDTO;
import org.example.dto.response.AuthResponseDTO;
import org.example.dto.response.LoginDTO;
import org.example.entity.RefreshToken;
import org.example.entity.User;
import org.example.exception.TokenRefreshException;
import org.example.exception.extend.UsernameTakenException;
import org.example.repo.RefreshTokenRepo;
import org.example.repo.UserRepo;
import org.example.sequrity.JwtUtils;
import org.example.sequrity.UserDetailsImpl;
import org.example.sequrity.service.RefreshTokenService;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder encoder;
    private final UserRepo userRepo;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepo refreshTokenRepo;

    @Transactional
    public void registerUser(SignUpDTO signUpDTO) {
        if (userRepo.existsByUsername(signUpDTO.username())) {
            throw new UsernameTakenException("Error: Username is taken", Errors.USERNAME_TAKEN);
        }
        if (userRepo.existsByEmail(signUpDTO.email())) {
            throw new UsernameTakenException("Error: Email is taken", Errors.EMAIL_TAKEN);
        }

        User user = new User();
        user.setUsername(signUpDTO.username());
        user.setEmail(signUpDTO.email());
        user.setPassword(encoder.encode(signUpDTO.password()));

        userRepo.save(user);
    }

    @Transactional
    public AuthResponseDTO signIn(LoginUserDTO loginDTO) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        ResponseCookie jwtRefreshCookie = jwtUtils.generateRefreshJwtCookie(refreshToken.getToken());

        LoginDTO userLoginDto = new LoginDTO(userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles);

        return new AuthResponseDTO(userLoginDto, jwtCookie.toString(), jwtRefreshCookie.toString());
    }

    public AuthResponseDTO signOut() {
        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setAccessToken(jwtUtils.getCleanJwtCookie().toString());
        authResponseDTO.setRefreshToken(jwtUtils.getCleanJwtRefreshCookie().toString());
        return authResponseDTO;
    }

    public AuthResponseDTO refreshToken(HttpServletRequest request) {
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);
        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new TokenRefreshException(refreshToken, "Refresh token is empty!");
        }
        return refreshTokenRepo.findByToken(refreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);
                    AuthResponseDTO authResponseDTO = new AuthResponseDTO();
                    authResponseDTO.setAccessToken(jwtCookie.toString());

                    return authResponseDTO;
                })
                .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in the database!"));

    }
}
