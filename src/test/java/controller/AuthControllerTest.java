package controller;

import jakarta.servlet.http.HttpServletRequest;
import org.example.controller.AuthController;
import org.example.controlleradvice.Errors;
import org.example.controlleradvice.SimpleResponse;
import org.example.dto.request.LoginUserDTO;
import org.example.dto.request.SignUpDTO;
import org.example.dto.response.AuthResponseDTO;
import org.example.dto.response.LoginDTO;
import org.example.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;


class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        SignUpDTO signUpDTO = new SignUpDTO("testUser", "test@example.com", "password123");

        doNothing().when(authService).registerUser(signUpDTO);

        ResponseEntity<?> response = authController.registerUser(signUpDTO);

        assertEquals(OK, response.getStatusCode());
        assertInstanceOf(SimpleResponse.class, response.getBody());
        assertEquals("User registered successfully!", ((SimpleResponse) response.getBody()).message());
    }

    @Test
    void signInUser_Success() {
        LoginUserDTO loginDTO = new LoginUserDTO("test@example.com", "password123");
        LoginDTO userDTO = new LoginDTO(1L, "testUser", "test@example.com", null);
        AuthResponseDTO authResponse = new AuthResponseDTO(userDTO, "accessToken", "refreshToken");

        when(authService.signIn(loginDTO)).thenReturn(authResponse);

        ResponseEntity<?> response = authController.signInUser(loginDTO);

        assertEquals(OK, response.getStatusCode());
        assertEquals(userDTO, response.getBody());
    }

    @Test
    void signInUser_Failure_InvalidCredentials() {
        LoginUserDTO loginDTO = new LoginUserDTO("test@example.com", "wrongPassword");

        when(authService.signIn(loginDTO)).thenThrow(new AuthenticationException("Invalid credentials") {});

        ResponseEntity<?> response = authController.signInUser(loginDTO);

        assertEquals(UNAUTHORIZED, response.getStatusCode());
        assertInstanceOf(SimpleResponse.class, response.getBody());
        assertEquals("Invalid username or password", ((SimpleResponse) response.getBody()).message());
        assertEquals(Errors.BAD_CREDENTIALS, ((SimpleResponse) response.getBody()).errorCode());
    }

    @Test
    void signOutUser_Success() {
        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setAccessToken("cleanAccessToken");
        authResponse.setRefreshToken("cleanRefreshToken");

        when(authService.signOut()).thenReturn(authResponse);

        ResponseEntity<?> response = authController.signOutUser();

        assertEquals(OK, response.getStatusCode());
        assertInstanceOf(SimpleResponse.class, response.getBody());
        assertEquals("You've been signed out!", ((SimpleResponse) response.getBody()).message());
    }

    @Test
    void refreshToken_Success() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        AuthResponseDTO authResponse = new AuthResponseDTO();
        authResponse.setAccessToken("newAccessToken");

        when(authService.refreshToken(request)).thenReturn(authResponse);

        ResponseEntity<?> response = authController.refreshToken(request);

        assertEquals(OK, response.getStatusCode());
        assertInstanceOf(SimpleResponse.class, response.getBody());
        assertEquals("Token is refreshed successfully!", ((SimpleResponse) response.getBody()).message());
    }
}
