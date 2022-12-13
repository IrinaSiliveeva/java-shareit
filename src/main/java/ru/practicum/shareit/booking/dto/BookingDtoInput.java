package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class BookingDtoInput {
    @NotNull
    private Long itemId;
    @NotNull
    @Future
    @JsonProperty("start")
    private LocalDateTime start;
    @NotNull
    @Future
    @JsonProperty("end")
    private LocalDateTime end;
}