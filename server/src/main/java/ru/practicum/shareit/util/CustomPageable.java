package ru.practicum.shareit.util;

import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.BadRequestException;

@EqualsAndHashCode
public class CustomPageable implements Pageable {
    private final int offset;
    private final int limit;
    private final Sort sort;

    public CustomPageable(int offset, int limit, Sort sort) {
        this.offset = offset;
        this.limit = limit;
        this.sort = sort;
    }

    public static Pageable of(Integer from, Integer size) {
        if (from == null && size == null) {
            from = 0;
            size = 50;
        }
        if (from < 0 || size <= 0) {
            throw new BadRequestException("Отрицальное значиение");
        }
        return new CustomPageable(from, size, Sort.unsorted());
    }

    @Override
    public int getPageNumber() {
        return 0;
    }

    @Override
    public int getPageSize() {
        return limit;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new CustomPageable(offset + limit, limit, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return new CustomPageable(offset, limit, sort);
    }

    @Override
    public Pageable first() {
        return new CustomPageable(offset, limit, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new CustomPageable(offset + limit * pageNumber, limit, sort);
    }

    @Override
    public boolean hasPrevious() {
        return false;
    }
}
