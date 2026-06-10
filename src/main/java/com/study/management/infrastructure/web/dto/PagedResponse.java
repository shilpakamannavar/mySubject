package com.study.management.infrastructure.web.dto;

import java.util.List;

public class PagedResponse<T> {

    private List<T> data;
    private Pagination pagination;

    public PagedResponse() {}

    public PagedResponse(List<T> data, Pagination pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public static <T> PagedResponse<T> of(List<T> data, int page, int size, long totalItems) {
        int totalPages = size == 0 ? 0 : (int) Math.ceil((double) totalItems / size);
        return new PagedResponse<>(data, new Pagination(page, size, totalItems, totalPages));
    }

    public List<T> getData()                { return data; }
    public void setData(List<T> data)       { this.data = data; }

    public Pagination getPagination()               { return pagination; }
    public void setPagination(Pagination pagination) { this.pagination = pagination; }

    public static class Pagination {
        private int page;
        private int size;
        private long totalItems;
        private int totalPages;

        public Pagination() {}

        public Pagination(int page, int size, long totalItems, int totalPages) {
            this.page = page;
            this.size = size;
            this.totalItems = totalItems;
            this.totalPages = totalPages;
        }

        public int getPage()                    { return page; }
        public void setPage(int page)           { this.page = page; }
        public int getSize()                    { return size; }
        public void setSize(int size)           { this.size = size; }
        public long getTotalItems()             { return totalItems; }
        public void setTotalItems(long v)       { this.totalItems = v; }
        public int getTotalPages()              { return totalPages; }
        public void setTotalPages(int v)        { this.totalPages = v; }
    }
}
