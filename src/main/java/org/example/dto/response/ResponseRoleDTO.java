package org.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.ERole;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseRoleDTO {
    private ERole roleName;
}
