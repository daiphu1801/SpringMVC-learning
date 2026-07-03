package com.examp.springmvc.shared.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PagedResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private final List<T> items;
    private final int page;
    private final int size;
    private final long totalItems;
    private final int totalPages;

    public PagedResult(List<T> items, int page, int size, long totalItems) {
        this.items = items != null ? new ArrayList<>(items) : new ArrayList<>();
        this.page = page;
        this.size = size;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / size);
    }

    public List<T> getItems() {
        return items != null ? Collections.unmodifiableList(items) : Collections.emptyList();
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean isHasPrevious() {
        return page > 1;
    }

    public boolean isHasNext() {
        return page < totalPages;
    }
}
