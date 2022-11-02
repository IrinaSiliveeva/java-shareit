package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput createBooking(@Valid @RequestBody BookingDtoInput bookingDtoInput,
                                          @RequestHeader("X-Sharer-User-Id") @NotNull long bookerId) {
        return bookingService.createBooking(bookingDtoInput, bookerId);
    }

    @GetMapping("{bookingId}")
    public BookingDtoOutput getBooking(
            @PathVariable Long bookingId,
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @PatchMapping("{bookingId}")
    public BookingDtoOutput approve(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @PathVariable @NotNull Long bookingId,
            @RequestParam @NotNull Boolean approved
    ) {
        return bookingService.approveBooking(bookingId, userId, approved);
    }

    @GetMapping
    public Collection<BookingDtoOutput> getAllBookingByUser(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long userId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingByUser(userId, state);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOutput> getAllBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
            @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingByOwner(ownerId, state);
    }
}