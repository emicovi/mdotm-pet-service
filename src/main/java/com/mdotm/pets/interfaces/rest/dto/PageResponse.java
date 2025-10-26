package com.mdotm.pets.interfaces.rest.dto;

import java.util.List;

public record PageResponse<T>(List<T> content, long totalElements, int page, int size) { }
