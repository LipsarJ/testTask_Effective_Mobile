package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("register")
    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает сообщение об успешной регистрации пользователя",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Если данные пользователя некорректны",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpDTO signUpDTO) {
        authService.registerUser(signUpDTO);
        return ResponseEntity.ok(new SimpleResponse("User registered successfully!", null));
    }

    @PostMapping("signin")
    @Operation(summary = "Аутентификация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает информацию о пользователе с токенами",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = LoginDTO.class))),
            @ApiResponse(responseCode = "401", description = "Если неверные данные для аутентификации",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class)))
    })
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
    @ApiResponse(responseCode = "200", description = "Возвращает сообщение о успешном выходе пользователя",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SimpleResponse.class)))
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Возвращает сообщение об успешном обновлении токена",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class))),
            @ApiResponse(responseCode = "400", description = "Если токен обновления недействителен",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = SimpleResponse.class)))
    })
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {

        AuthResponseDTO authResponseDTO = authService.refreshToken(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, authResponseDTO.getAccessToken())
                .body(new SimpleResponse("Token is refreshed successfully!", null));
    }
}
