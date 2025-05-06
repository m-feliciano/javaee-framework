package com.dev.servlet.pojo.records;

import com.dev.servlet.pojo.Pageable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @since 1.4
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
public final class Query {
    private final Pageable pageable;
    private final String search;
    private final String type;
}