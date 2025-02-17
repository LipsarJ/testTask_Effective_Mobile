package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.controlleradvice.CommonErrorApiResponses;
import org.example.controlleradvice.CommonErrorApiResponsesWith404;
import org.example.controlleradvice.Errors;
import org.example.controlleradvice.SimpleResponse;
import org.example.dto.request.LoginUserDTO;
import org.example.dto.request.SignUpDTO;
import org.example.dto.response.AuthResponseDTO;
import org.example.dto.response.LoginDTO;
import org.example.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.example.constructor.ApiConstants.*;

@RestController
@RequestMapping(PUBLIC_V1_AUTH)
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    @Operation(summary = "Регистрация пользователя")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SimpleResponse.class)))
    @CommonErrorApiResponses

    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDTO signUpDTO) {
        authService.registerUser(signUpDTO);
        return ResponseEntity.ok(new SimpleResponse("User registered successfully!", null));
    }

    @PostMapping("signin")
    @Operation(summary = "Аутентификация пользователя")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = LoginDTO.class)))
    @CommonErrorApiResponses
    public ResponseEntity<?> signInUser(@Valid @RequestBody LoginUserDTO loginDto) {
        try {
            AuthResponseDTO response = authService.signIn(loginDto);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, response.getAccessToken())
                    .header(HttpHeaders.SET_COOKIE, response.getRefreshToken())
                    .body(response.getUserDTO());

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(new SimpleResponse("Invalid username or password", Errors.BAD_CREDENTIALS));
        }
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("signout")
    @Operation(summary = "Выход пользователя")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SimpleResponse.class)))
    @CommonErrorApiResponses
    public ResponseEntity<?> signOutUser() {

        AuthResponseDTO response = authService.signOut();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, response.getAccessToken())
                .header(HttpHeaders.SET_COOKIE, response.getRefreshToken())
                .body(new SimpleResponse("You've been signed out!", null));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("refreshtoken")
    @Operation(summary = "Обновление токена")
    @ApiResponse(responseCode = "200", description = HTTP_OK_200_DESCRIPTION,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SimpleResponse.class)))
    @CommonErrorApiResponsesWith404
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {

        AuthResponseDTO authResponseDTO = authService.refreshToken(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponseDTO.getAccessToken())
                .body(new SimpleResponse("Token is refreshed successfully!", null));
    }
}
