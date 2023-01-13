package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class BookingDtoInput {
    private Long itemId;
    @JsonProperty("start")
    private LocalDateTime start;
    @JsonProperty("end")
    private LocalDateTime end;
}