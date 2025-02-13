package org.example.mapper;

import org.example.dto.response.ResponseUserDTO;
import org.example.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BaseLocalDateTimeOffsetDateTimeMapper.class)
public interface UserMapper {

    @Mapping(target = "roles", source = "user.roles")
    ResponseUserDTO toResponseUserDTO(User user);
}
