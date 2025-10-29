package com.avijitmondal.ops.dto;

import java.util.List;

/**
 * Generic pagination response wrapper.
 * @param <T> item type
 */
public record PageResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last,
        String sort,
        String direction
) {
    public static <T> PageResponse<T> of(org.springframework.data.domain.Page<T> page) {
        String sort = page.getSort().isSorted() 
            ? page.getSort().iterator().next().getProperty() 
            : "unsorted";
        String direction = page.getSort().isSorted() 
            ? page.getSort().iterator().next().getDirection().name() 
            : "NONE";
        
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                sort,
                direction
        );
    }
}
