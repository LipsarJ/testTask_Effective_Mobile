package org.example.controlleradvice;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)

@ApiResponse(responseCode = "400", description = "Ошибка клиентского запроса",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SimpleResponse.class)))
@ApiResponse(responseCode = "400", description = "Ошибка авторизации",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SimpleResponse.class)))
@ApiResponse(responseCode = "403", description = "Запрет доступа",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SimpleResponse.class)))
@ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
        content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = SimpleResponse.class)))


public @interface CommonErrorApiResponses {
}
