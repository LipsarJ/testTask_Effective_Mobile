package service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.dto.request.LoginUserDTO;
import org.example.dto.request.SignUpDTO;
import org.example.dto.response.AuthResponseDTO;
import org.example.entity.RefreshToken;
import org.example.entity.User;
import org.example.exception.TokenRefreshException;
import org.example.exception.extend.UsernameTakenException;
import org.example.repo.RefreshTokenRepo;
import org.example.repo.UserRepo;
import org.example.sequrity.JwtUtils;
import org.example.sequrity.UserDetailsImpl;
import org.example.sequrity.service.RefreshTokenService;
import org.example.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private RefreshTokenRepo refreshTokenRepo;

    @Mock
    private PasswordEncoder encoder;


    @Mock
    private HttpServletRequest request;

    @Mock
    private UserDetailsImpl userDetails;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Test
    public void testRegisterUser_usernameTaken_shouldThrowException() {

        SignUpDTO signUpDTO = new SignUpDTO("testUser", "testemail@example.com", "password123");
        when(userRepo.existsByUsername(signUpDTO.username())).thenReturn(true);

        try {
            authService.registerUser(signUpDTO);
        } catch (UsernameTakenException e) {

            verify(userRepo).existsByUsername(signUpDTO.username());
            verify(userRepo, never()).existsByEmail(signUpDTO.email());
            assert e.getMessage().equals("Error: Username is taken");
        }
    }

    @Test
    public void testRegisterUser_success() {

        SignUpDTO signUpDTO = new SignUpDTO("testUser", "testemail@example.com", "password123");
        when(userRepo.existsByUsername(signUpDTO.username())).thenReturn(false);
        when(userRepo.existsByEmail(signUpDTO.email())).thenReturn(false);

        authService.registerUser(signUpDTO);

        verify(userRepo).existsByUsername(signUpDTO.username());
        verify(userRepo).existsByEmail(signUpDTO.email());
        verify(userRepo).save(any());
    }

    @Test
    public void testSignIn_success() {

        LoginUserDTO loginDTO = new LoginUserDTO("testemail@example.com", "password123");

        String refreshToken = "valid_refresh_token";
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getId()).thenReturn(1L);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getEmail()).thenReturn("testemail@example.com");
        when(jwtUtils.generateJwtCookie(userDetails)).thenReturn(ResponseCookie.from("access_token", "mock_token").build());
        when(refreshTokenService.createRefreshToken(1L)).thenReturn(refreshTokenEntity);
        when(jwtUtils.generateRefreshJwtCookie(refreshToken)).thenReturn(ResponseCookie.from("refresh_token", "mock_token_refresh").build());

        AuthResponseDTO response = authService.signIn(loginDTO);


        assertNotNull(response);
        assertEquals("access_token=mock_token", response.getAccessToken());
        assertEquals("refresh_token=mock_token_refresh", response.getRefreshToken());
    }

    @Test
    public void testSignOut() {

        when(jwtUtils.getCleanJwtCookie()).thenReturn(ResponseCookie.from("access_token", "empty_token").build());
        when(jwtUtils.getCleanJwtRefreshCookie()).thenReturn(ResponseCookie.from("refresh_token", "empty_token").build());

        AuthResponseDTO response = authService.signOut();


        assertNotNull(response);
        assertEquals("access_token=empty_token", response.getAccessToken());
        assertEquals("refresh_token=empty_token", response.getRefreshToken());
    }

    @Test
    public void testRefreshToken_success() {

        String refreshToken = "valid_refresh_token";
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setId(1L);
        refreshTokenEntity.setToken(refreshToken);
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");
        refreshTokenEntity.setUser(user);
        when(jwtUtils.getJwtRefreshFromCookies(request)).thenReturn(refreshToken);
        when(refreshTokenRepo.findByToken(refreshToken)).thenReturn(java.util.Optional.of(refreshTokenEntity));
        when(refreshTokenService.verifyExpiration(any())).thenReturn(refreshTokenEntity);
        when(jwtUtils.generateJwtCookie(user)).thenReturn(ResponseCookie.from("access_token", "mock_token").build());


        AuthResponseDTO response = authService.refreshToken(request);


        assertNotNull(response);
        assertEquals("access_token=mock_token", response.getAccessToken());
    }

    @Test
    public void testRefreshToken_invalidToken_shouldThrowException() {
        String refreshToken = "invalid_refresh_token";
        when(jwtUtils.getJwtRefreshFromCookies(request)).thenReturn(refreshToken);
        when(refreshTokenRepo.findByToken(refreshToken)).thenReturn(java.util.Optional.empty());

        assertThrows(TokenRefreshException.class, () -> authService.refreshToken(request));
    }
}
