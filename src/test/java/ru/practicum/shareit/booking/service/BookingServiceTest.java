package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.CustomPageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @InjectMocks
    private BookingService bookingService;
    private Booking booking;
    private BookingDtoInput bookingDtoInput;
    private BookingDtoOutput bookingDtoOutput;
    private User user;
    private Item item;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("user").email("user@user.ru").build();
        item = Item.builder().owner(User.builder().id(2L).name("user").email("1@2.ru").build())
                .available(true).name("item").description("test").build();
        booking = Booking.builder().id(1L).start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(10)).status(Status.WAITING).item(item).booker(user).build();
        bookingDtoOutput = BookingMapper.toBookingDtoOutput(booking);
        bookingDtoInput = BookingDtoInput.builder().itemId(1L).start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(10)).build();
    }

    @Test
    void shouldCreateBooking() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDtoOutput result = bookingService.createBooking(bookingDtoInput, anyLong());
        assertThat(result).isNotNull().isEqualTo(bookingDtoOutput);
        verify(bookingRepository).save(any());
    }

    @Test
    void shouldCreateBookingIfBookerIsOwner() {
        booking.getBooker().setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(bookingDtoInput, 2L));
    }

    @Test
    void shouldCreateBookingIsItemNotAvailable() {
        item.setAvailable(false);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, anyLong()));
    }

    @Test
    void shouldCreateBookingIfStartTimeInPast() {
        bookingDtoInput.setStart(LocalDateTime.MIN);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, anyLong()));
    }

    @Test
    void shouldCreateBookingIfEndTimeInPast() {
        bookingDtoInput.setEnd(LocalDateTime.MIN);
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, anyLong()));
    }

    @Test
    void shouldCreateBookingIfEndTimeIsBeforeStart() {
        bookingDtoInput.setEnd(LocalDateTime.now().minusDays(10));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(bookingDtoInput, anyLong()));
    }

    @Test
    void shouldGetBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        BookingDtoOutput result = bookingService.getBooking(1L, 1L);
        assertThat(result).isNotNull().isEqualTo(bookingDtoOutput);
    }

    @Test
    void shouldGetBookingIfUserDontBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        assertThrows(NotFoundException.class, () -> bookingService.getBooking(1L, 40L));
    }

    @Test
    void shouldApproveBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDtoOutput result = bookingService.approveBooking(1L, 2L, true);
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(bookingDtoOutput.getId());
        verify(bookingRepository).save(any());
    }

    @Test
    void shouldApproveBookingIfUserNotOwner() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(1L, 1L, true));
    }

    @Test
    void shouldApproveBookingIfAllReadyApproved() {
        booking.setStatus(Status.APPROVED);
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        assertThrows(BadRequestException.class, () -> bookingService.approveBooking(1L, 2L, true));
    }

    @Test
    void shouldRejectBooking() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.ofNullable(booking));
        when(bookingRepository.save(any())).thenReturn(booking);
        BookingDtoOutput result = bookingService.approveBooking(1L, 2L, false);
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo(Status.REJECTED);
        verify(bookingRepository).save(any());
    }

    @Test
    void shouldGetAllBookingByUserWithStateAll() {
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBooker_IdOrderByEndDesc(2L, CustomPageable.of(0, 5)))
                .thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByUser(2L, "ALL", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        verify(bookingRepository).findAllByBooker_IdOrderByEndDesc(2L, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingWhenUserNotFound() {
        when(userRepository.findById(2L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () ->
                bookingService.getAllBookingByUser(2L, "ALL", 0, 5));
    }

    @Test
    void shouldGetAllBookingWhenStateUnknown() {
        assertThrows(BadRequestException.class, () ->
                bookingService.getAllBookingByUser(2L, "abc", 0, 5));
    }

    @Test
    void shouldGetAllBookingByUserWithStateWaiting() {
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBooker_IdAndStatusOrderByEndDesc(2L, Status.WAITING,
                CustomPageable.of(0, 5))).thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByUser(2L, "WAITING", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository).findAllByBooker_IdAndStatusOrderByEndDesc(2L, Status.WAITING,
                CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByUserWithStateReject() {
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBooker_IdAndStatusOrderByEndDesc(2L, Status.REJECTED,
                CustomPageable.of(0, 5))).thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByUser(2L, "REJECTED", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository).findAllByBooker_IdAndStatusOrderByEndDesc(2L, Status.REJECTED,
                CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByUserWithStatePast() {
        booking.setEnd(LocalDateTime.now().minusDays(10));
        booking.setStart(LocalDateTime.now().minusDays(5));
        bookingDtoOutput = BookingMapper.toBookingDtoOutput(booking);
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBooker_IdOrderByEndDesc(2L, CustomPageable.of(0, 5)))
                .thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByUser(2L, "PAST", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository).findAllByBooker_IdOrderByEndDesc(2L, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByUserWithStateCurrent() {
        booking.setStart(LocalDateTime.now().minusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        bookingDtoOutput = BookingMapper.toBookingDtoOutput(booking);
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBooker_IdOrderByEndDesc(2L, CustomPageable.of(0, 5)))
                .thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByUser(2L, "CURRENT", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository).findAllByBooker_IdOrderByEndDesc(2L, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByUserWithStateFuture() {
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        bookingDtoOutput = BookingMapper.toBookingDtoOutput(booking);
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBooker_IdOrderByEndDesc(2L, CustomPageable.of(0, 5)))
                .thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByUser(2L, "FUTURE", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository).findAllByBooker_IdOrderByEndDesc(2L, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByOwnerWithStateAll() {
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerId(2L, CustomPageable.of(0, 5)))
                .thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByOwner(2L, "ALL", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository).findAllByOwnerId(2L, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByOwnerWithStateWaiting() {
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerIdAndStatus(2L, Status.WAITING, CustomPageable.of(0, 5)))
                .thenReturn(List.of(booking));
        List<BookingDtoOutput> result = bookingService.getAllBookingByOwner(2L, "WAITING", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository)
                .findAllByOwnerIdAndStatus(2L, Status.WAITING, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByOwnerWithStateReject() {
        booking.setStatus(Status.REJECTED);
        bookingDtoOutput = BookingMapper.toBookingDtoOutput(booking);
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerIdAndStatus(2L, Status.REJECTED, CustomPageable.of(0, 5)))
                .thenReturn(List.of(booking));
        List<BookingDtoOutput> result = bookingService.getAllBookingByOwner(2L, "REJECTED", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository)
                .findAllByOwnerIdAndStatus(2L, Status.REJECTED, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByOwnerWithStatePast() {
        booking.setStart(LocalDateTime.now().minusDays(10));
        booking.setEnd(LocalDateTime.now().minusDays(5));
        bookingDtoOutput = BookingMapper.toBookingDtoOutput(booking);
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerId(2L, CustomPageable.of(0, 5)))
                .thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByOwner(2L, "PAST", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository).findAllByOwnerId(2L, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByOwnerWithStateCurrent() {
        booking.setStart(LocalDateTime.now().minusDays(5));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        bookingDtoOutput = BookingMapper.toBookingDtoOutput(booking);
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerId(2L, CustomPageable.of(0, 5)))
                .thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByOwner(2L, "CURRENT", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository).findAllByOwnerId(2L, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllBookingByOwnerWithStateFuture() {
        booking.setStart(LocalDateTime.now().plusDays(2));
        booking.setEnd(LocalDateTime.now().plusDays(5));
        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(booking));
        bookingDtoOutput = BookingMapper.toBookingDtoOutput(booking);
        when(userRepository.findById(2L)).thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerId(2L, CustomPageable.of(0, 5)))
                .thenReturn(bookingPage);
        List<BookingDtoOutput> result = bookingService.getAllBookingByOwner(2L, "FUTURE", 0, 5)
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(bookingDtoOutput);
        verify(bookingRepository).findAllByOwnerId(2L, CustomPageable.of(0, 5));
    }
}
