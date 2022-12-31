package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput createBooking(@Valid @RequestBody BookingDtoInput bookingDtoInput,
                                          @RequestHeader("X-Sharer-User-Id") @NotNull long bookerId) {
        log.info("создано бронироваине: " + bookingDtoInput);
        return bookingService.createBooking(bookingDtoInput, bookerId);
    }

    @GetMapping("{bookingId}")
    public BookingDtoOutput getBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        log.info("запрос на получение бронирования под id: " + bookingId + " от пользователя под id: " + userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoOutput approve(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @PathVariable @NotNull Long bookingId,
            @RequestParam @NotNull Boolean approved
    ) {
        log.info("бронирование под id: " + bookingId + " изменен статус брони: " +
                approved + " пользователем под id:" + userId);
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping
    public Collection<BookingDtoOutput> getAllBookingByUser(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
            @Positive @RequestParam(name = "size", required = false) Integer size) {
        log.info("получение всех бронирований пользователем под id: " + userId);
        return bookingService.getAllBookingByUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOutput> getAllBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
            @RequestParam(defaultValue = "ALL") String state,
            @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
            @Positive @RequestParam(name = "size", required = false) Integer size) {
        log.info("получение владельцом под id: " + ownerId + " списка бронирования своих вещей");
        return bookingService.getAllBookingByOwner(ownerId, state, from, size);
    }
}