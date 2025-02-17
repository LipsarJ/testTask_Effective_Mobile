package org.example.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Страница с информацией о сущностях")
public class PageDTO<T> {
    private List<T> info;
    private long totalCount;
    private int page;
    private int countValuesPerPage;
}