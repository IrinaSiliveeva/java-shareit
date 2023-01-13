package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class ItemRequestDto {
    @Positive
    private Long id;
    private LocalDateTime created;
    @NotBlank
    private String description;
    private Long requesterId;
    private List<ItemDto> items;
}
