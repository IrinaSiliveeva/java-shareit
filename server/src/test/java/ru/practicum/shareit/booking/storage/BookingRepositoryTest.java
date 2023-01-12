package ru.practicum.shareit.booking.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.CustomPageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Booking booking;
    private Booking fistBooking;
    private User itemOwner;
    private User booker;
    private Item item;

    @BeforeEach
    void setup() {
        itemOwner = User.builder().name("user").email("user@user.ru").build();
        itemOwner = userRepository.save(itemOwner);
        booker = User.builder().name("booker").email("booker@booker.ru").build();
        booker = userRepository.save(booker);
        item = Item.builder().description("test").name("item").owner(itemOwner).available(true).build();
        item = itemRepository.save(item);
        booking = Booking.builder().booker(booker).start(LocalDateTime.now()).end(LocalDateTime.now().plusDays(5))
                .status(Status.WAITING).item(item).build();
        booking = bookingRepository.save(booking);
        fistBooking = Booking.builder().booker(booker).start(LocalDateTime.now().minusDays(10))
                .end(LocalDateTime.now().minusDays(5)).status(Status.APPROVED).item(item).build();
        fistBooking = bookingRepository.save(fistBooking);
    }

    @Test
    void shouldGetFirstBookingByItemId() {
        Booking result = bookingRepository.getFirstByItem_IdOrderByStartAsc(item.getId());
        assertThat(result).isNotNull().isEqualTo(fistBooking);
    }

    @Test
    void shouldGetLastBookingByItemId() {
        Booking result = bookingRepository.getFirstByItem_IdOrderByEndDesc(item.getId());
        assertThat(result).isNotNull().isEqualTo(booking);
    }

    @Test
    void shouldCheckExistBookerIsTrue() {
        boolean result = bookingRepository.existsAllByBooker_IdAndEndBefore(booker.getId(), LocalDateTime.now());
        assertThat(result).isTrue();
    }

    @Test
    void shouldCheckExistBookerIsFalse() {
        boolean result = bookingRepository.existsAllByBooker_IdAndEndBefore(40L, LocalDateTime.now());
        assertThat(result).isFalse();
    }

    @Test
    void shouldFindAllByBookerId() {
        List<Booking> result = bookingRepository
                .findAllByBooker_IdOrderByEndDesc(booker.getId(), CustomPageable.of(0, 5))
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(booking);
    }

    @Test
    void shouldFindAllByBookerIdAndStatus() {
        List<Booking> result = new ArrayList<>(bookingRepository
                .findAllByOwnerIdAndStatus(itemOwner.getId(), booking.getStatus(), CustomPageable.of(0, 5)));
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(booking);
    }

    @Test
    void shouldFindAllByOwnerId() {
        List<Booking> result = bookingRepository.findAllByOwnerId(itemOwner.getId(), CustomPageable.of(0, 5))
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(booking);
    }

    @Test
    void shouldFindAllByOwnerIdAndStatus() {
        List<Booking> result = new ArrayList<>(bookingRepository
                .findAllByOwnerIdAndStatus(itemOwner.getId(), booking.getStatus(), CustomPageable.of(0, 5)));
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(booking);
    }
}
