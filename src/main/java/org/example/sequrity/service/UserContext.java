package org.example.sequrity.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.example.dto.response.ResponseUserDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope
@Getter
@Setter(AccessLevel.PACKAGE)
public class UserContext {
    private ResponseUserDTO userDTO;
}

