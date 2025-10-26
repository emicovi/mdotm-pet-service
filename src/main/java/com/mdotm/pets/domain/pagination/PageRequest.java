package com.mdotm.pets.domain.pagination;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public record PageRequest(int page, int size, List<SortOrder> sort) {
    public PageRequest {
        if (page < 0) throw new IllegalArgumentException("page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("size must be > 0");
        sort = sort == null ? List.of() : Collections.unmodifiableList(new ArrayList<>(sort));
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size, List.of());
    }
}
