package controller;

import org.example.controller.AuthController;
import org.example.dto.request.LoginUserDTO;
import org.example.dto.request.SignUpDTO;
import org.example.entity.User;
import org.example.repo.UserRepo;
import org.example.sequrity.JwtUtils;
import org.example.sequrity.UserDetailsImpl;
import org.example.sequrity.service.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private UserRepo userRepo;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserDetailsImpl userDetails;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
    }

    @Test
    void registerUser_ShouldReturnSuccess() {
        SignUpDTO signUpDTO = new SignUpDTO("testUser", "test@example.com", "password");

        when(userRepo.existsByUsername(signUpDTO.username())).thenReturn(false);
        when(userRepo.existsByEmail(signUpDTO.email())).thenReturn(false);
        when(userRepo.save(any(User.class))).thenReturn(testUser);
        passwordEncoder = new BCryptPasswordEncoder();

        ResponseEntity<?> response = authController.registerUser(signUpDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepo, times(1)).save(any(User.class));
    }

    @Test
    void signInUser_ShouldReturnJwtTokens() {
        LoginUserDTO loginUserDTO = new LoginUserDTO("test@example.com", "password");

        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(null);
        when(jwtUtils.generateJwtCookie((UserDetailsImpl) any())).thenReturn(null);
        when(refreshTokenService.createRefreshToken(any())).thenReturn(null);
        userDetails = new UserDetailsImpl(1L, "testUser", "test@example.com", "password", new ArrayList<>());

        ResponseEntity<?> response = authController.signInUser(loginUserDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void signOutUser_ShouldReturnSuccess() {
        when(jwtUtils.getCleanJwtCookie()).thenReturn(null);
        when(jwtUtils.getCleanJwtRefreshCookie()).thenReturn(null);

        ResponseEntity<?> response = authController.signOutUser();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}

