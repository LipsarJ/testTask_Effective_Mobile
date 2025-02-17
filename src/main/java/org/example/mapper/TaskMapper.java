package org.example.mapper;

import org.example.dto.response.ResponseTaskDTO;
import org.example.entity.Task;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = BaseLocalDateTimeOffsetDateTimeMapper.class)
public interface TaskMapper {

    ResponseTaskDTO toResponseTaskDTO(Task task);
}
