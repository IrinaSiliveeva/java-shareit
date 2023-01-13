package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.client.BookingClient;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getAllBookingByUser(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                      @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                      @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Получение бронирования с состоянием={}, пользователь={}, от={}, размер={}", stateParam, userId, from, size);
        return bookingClient.getAllBookingByUser(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestBody @Valid BookItemRequestDto requestDto) {
        log.info("Создание бронирования {}, пользователь={}", requestDto, userId);
        return bookingClient.createBooking(userId, requestDto);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long bookingId) {
        log.info("Получение бронирования {}, пользователь={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                                       @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                       @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                       @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Владелец получает брони с состоянием {}, пользователь={}, от={}, размер={}", stateParam, ownerId, from, size);
        return bookingClient.getAllBookingByOwner(ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @RequestParam Boolean approved) {
        log.info("Пользователь изменил статус потверждения={}, бронирование={}, пользователь={}", approved, bookingId, ownerId);
        return bookingClient.approve(bookingId, ownerId, approved);
    }
}
