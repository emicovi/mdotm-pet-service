package com.mdotm.pets.domain.pagination;

import java.util.Objects;

public record SortOrder(String property, Direction direction) {
    public SortOrder {
        Objects.requireNonNull(property, "property must not be null");
        Objects.requireNonNull(direction, "direction must not be null");
    }
}
