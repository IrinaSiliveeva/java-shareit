package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import java.util.Set;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Set<CommentDto> comments;
    private BookingDtoItem lastBooking;
    private BookingDtoItem nextBooking;
    private Long requestId;
}