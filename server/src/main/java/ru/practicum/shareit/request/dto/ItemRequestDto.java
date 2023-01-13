package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private LocalDateTime created;
    private String description;
    private Long requesterId;
    private List<ItemDto> items;
}
