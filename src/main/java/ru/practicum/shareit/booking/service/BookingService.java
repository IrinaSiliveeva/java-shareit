package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public BookingDtoOutput createBooking(BookingDtoInput bookingDtoInput, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден"));
        Item item = itemRepository.findById(bookingDtoInput.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмет с id " + bookingDtoInput.getItemId() + " не найден"));
        Booking booking = BookingMapper.fromBookingDtoInput(bookingDtoInput, user, item);
        booking.setStatus(Status.WAITING);
        checkBookingBeforeSave(booking);
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }

    private void checkBookingBeforeSave(Booking booking) {
        if (Objects.equals(booking.getBooker().getId(), booking.getItem().getOwner().getId())) {
            throw new NotFoundException("Пользователь является владельцем вещи");
        }
        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Предмет не доступен сейчас");
        }
        if (booking.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Время начала не может быть в прошлом");
        }
        if (booking.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Время окончания не может быть в прошлом");
        }
        if (booking.getEnd().isBefore(booking.getStart())) {
            throw new BadRequestException("Время начала после времени окончания");
        }
    }

    public BookingDtoOutput getBooking(Long bookingId, Long userId) {
        Booking booking = getBooking(bookingId);
        if (Objects.equals(booking.getBooker().getId(), userId) ||
                Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            return BookingMapper.toBookingDtoOutput(booking);
        }
        throw new NotFoundException("Неверный пользователь");
    }

    private Booking getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Букинг с id " + bookingId + " не найден"));
    }

    public BookingDtoOutput approveBooking(Long bookingId, Long userId, Boolean approve) {
        Booking booking = getBooking(bookingId);
        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Бронирование уже подтверждено");
        }
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException("Пользователь не владеет вещью");
        }
        booking.setStatus(approve ? Status.APPROVED : Status.REJECTED);
        return BookingMapper.toBookingDtoOutput(bookingRepository.save(booking));
    }

    private void checkStateAndUser(String state, Long userId) {
        if (!ObjectUtils.containsConstant(State.values(), state)) {
            throw new BadRequestException("Unknown state: " + state);
        }
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    public Collection<BookingDtoOutput> getAllBookingByUser(Long userId, String state) {
        checkStateAndUser(state, userId);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByBooker_IdOrderByEndDesc(userId).stream()
                        .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByBooker_IdAndStatusOrderByEndDesc(userId, Status.WAITING).stream()
                        .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByBooker_IdAndStatusOrderByEndDesc(userId, Status.REJECTED).stream()
                        .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "PAST":
                List<BookingDtoOutput> pastList = new ArrayList<>();
                for (Booking booking : bookingRepository.findAllByBooker_IdOrderByEndDesc(userId)) {
                    if (booking.getEnd().isBefore(LocalDateTime.now())) {
                        pastList.add(BookingMapper.toBookingDtoOutput(booking));
                    }
                }
                return pastList;
            case "CURRENT":
                List<BookingDtoOutput> currentList = new ArrayList<>();
                for (Booking booking : bookingRepository.findAllByBooker_IdOrderByEndDesc(userId)) {
                    if (booking.getStart().isBefore(LocalDateTime.now()) &&
                            booking.getEnd().isAfter(LocalDateTime.now())) {
                        currentList.add(BookingMapper.toBookingDtoOutput(booking));
                    }
                }
                return currentList;
            case "FUTURE":
                List<BookingDtoOutput> futureList = new ArrayList<>();
                for (Booking booking : bookingRepository.findAllByBooker_IdOrderByEndDesc(userId)) {
                    if (booking.getEnd().isAfter(LocalDateTime.now())) {
                        futureList.add(BookingMapper.toBookingDtoOutput(booking));
                    }
                }
                return futureList;
            default:
                return new ArrayList<>();
        }
    }

    public Collection<BookingDtoOutput> getAllBookingByOwner(Long ownerId, String state) {
        checkStateAndUser(state, ownerId);
        switch (state) {
            case "ALL":
                return bookingRepository.findAllByOwnerId(ownerId).stream()
                        .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "WAITING":
                return bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.WAITING).stream()
                        .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "REJECTED":
                return bookingRepository.findAllByOwnerIdAndStatus(ownerId, Status.REJECTED).stream()
                        .map(BookingMapper::toBookingDtoOutput).collect(Collectors.toList());
            case "PAST":
                List<BookingDtoOutput> pastList = new ArrayList<>();
                for (Booking booking : bookingRepository.findAllByOwnerId(ownerId)) {
                    if (booking.getEnd().isBefore(LocalDateTime.now())) {
                        pastList.add(BookingMapper.toBookingDtoOutput(booking));
                    }
                }
                return pastList;
            case "CURRENT":
                List<BookingDtoOutput> currentList = new ArrayList<>();
                for (Booking booking : bookingRepository.findAllByOwnerId(ownerId)) {
                    if (booking.getStart().isBefore(LocalDateTime.now()) &&
                            booking.getEnd().isAfter(LocalDateTime.now())) {
                        currentList.add(BookingMapper.toBookingDtoOutput(booking));
                    }
                }
                return currentList;
            case "FUTURE":
                List<BookingDtoOutput> futureList = new ArrayList<>();
                for (Booking booking : bookingRepository.findAllByOwnerId(ownerId)) {
                    if (booking.getEnd().isAfter(LocalDateTime.now())) {
                        futureList.add(BookingMapper.toBookingDtoOutput(booking));
                    }
                }
                return futureList;
            default:
                return new ArrayList<>();
        }
    }
}