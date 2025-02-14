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
import org.example.dto.response.LoginDTO;
import org.example.entity.RefreshToken;
import org.example.entity.User;
import org.example.exception.TokenRefreshException;
import org.example.repo.RefreshTokenRepo;
import org.example.repo.UserRepo;
import org.example.sequrity.JwtUtils;
import org.example.sequrity.UserDetailsImpl;
import org.example.sequrity.service.RefreshTokenService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PasswordEncoder encoder;
    private final UserRepo userRepo;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepo refreshTokenRepo;

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

        if (userRepo.existsByUsername(signUpDTO.username())) {
            return ResponseEntity.badRequest().body(new SimpleResponse("Error: Username is taken", Errors.USERNAME_TAKEN));
        }
        if (userRepo.existsByEmail(signUpDTO.email())) {
            return ResponseEntity.badRequest().body(new SimpleResponse("Error: Email is taken", Errors.EMAIL_TAKEN));
        }

        User user = new User();
        user.setUsername(signUpDTO.username());
        user.setEmail(signUpDTO.email());
        user.setPassword(encoder.encode(signUpDTO.password()));

        userRepo.save(user);

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
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginDto.email(), loginDto.password()));

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

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                    .body(userLoginDto);
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(new SimpleResponse("Invalid username or password", Errors.BAD_CREDENTIALS));
        }
    }

    @PostMapping("signout")
    @Operation(summary = "Выход пользователя")
    @ApiResponse(responseCode = "200", description = "Возвращает сообщение о успешном выходе пользователя",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = SimpleResponse.class)))
    public ResponseEntity<?> signOutUser() {

        ResponseCookie jwtCookie = jwtUtils.getCleanJwtCookie();
        ResponseCookie jwtRefreshCookie = jwtUtils.getCleanJwtRefreshCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .header(HttpHeaders.SET_COOKIE, jwtRefreshCookie.toString())
                .body(new SimpleResponse("You've been signed out!", null));
    }

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
        String refreshToken = jwtUtils.getJwtRefreshFromCookies(request);

        if (refreshToken != null && !refreshToken.isEmpty()) {
            return refreshTokenRepo.findByToken(refreshToken)
                    .map(refreshTokenService::verifyExpiration)
                    .map(RefreshToken::getUser)
                    .map(user -> {
                        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(user);

                        return ResponseEntity.ok()
                                .header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                                .body(new SimpleResponse("Token is refreshed successfully!", null));
                    })
                    .orElseThrow(() -> new TokenRefreshException(refreshToken, "Refresh token is not in the database!"));
        }

        return ResponseEntity.badRequest().body(new SimpleResponse("Refresh Token is empty!", null));
    }
}
