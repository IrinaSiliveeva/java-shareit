package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class CommentDto {
    @Positive
    private Long id;
    @NotEmpty
    private String text;
    private String authorName;
    private LocalDateTime created;
}