package ru.practicum.shareit.item.dto;

import lombok.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.Set;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class ItemDto {
    @Positive
    private Long id;
    @NotBlank(groups = Create.class)
    private String name;
    @NotBlank(groups = Create.class)
    private String description;
    @NotNull(groups = Create.class)
    private Boolean available;
    private Set<CommentDto> comments;
    private BookItemRequestDto lastBooking;
    private BookItemRequestDto nextBooking;
    private Long requestId;
}