package org.example.mapper;

import org.example.dto.response.ResponseUserDTO;
import org.example.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = BaseLocalDateTimeOffsetDateTimeMapper.class)
public interface UserMapper {

    ResponseUserDTO toResponseUserDTO(User user);
}
