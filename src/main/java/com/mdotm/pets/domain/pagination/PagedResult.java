package com.mdotm.pets.domain.pagination;

import java.util.List;

public record PagedResult<T>(List<T> content, long totalElements, int page, int size) { }
