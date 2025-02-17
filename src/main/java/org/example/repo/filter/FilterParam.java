package org.example.repo.filter;

import jakarta.annotation.Nullable;
import lombok.Data;

import java.util.List;

@Data
public class FilterParam {

    @Nullable
    private List<String> usernames;

    @Nullable
    private String filterText;
}
