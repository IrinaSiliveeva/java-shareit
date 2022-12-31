package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.CustomPageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRequestRepository requestRepository;
    @InjectMocks
    private ItemService itemService;
    private Item item;
    private ItemDto itemDto;
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("user").email("test@test.ru").build();
        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("item")
                .description("test")
                .build();
        itemDto = ItemMapper.toItemDto(item);
    }

    @Test
    void shouldCreateItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRepository.save(item)).thenReturn(item);
        ItemDto result = itemService.createItem(itemDto, 1L);
        assertThat(result).isNotNull().isEqualTo(itemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void shouldCreateItemWhenUserNotfound() {
        when(userRepository.findById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldCreateItemWithRequest() {
        ItemRequest itemRequest = ItemRequest.builder().items(List.of(item)).created(LocalDateTime.now())
                .requester(user).id(1L).description("test").build();
        itemDto.setRequestId(1L);
        item.setRequest(itemRequest);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(item)).thenReturn(item);
        ItemDto result = itemService.createItem(itemDto, 1L);
        assertThat(result).isNotNull().isEqualTo(itemDto);
        verify(itemRepository).save(item);
        verify(requestRepository).findById(1L);
    }

    @Test
    void shouldCreateItemWithNotFoundRequest() {
        ItemRequest itemRequest = ItemRequest.builder().items(List.of(item)).created(LocalDateTime.now())
                .requester(user).id(1L).description("test").build();
        itemDto.setRequestId(1L);
        item.setRequest(itemRequest);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.createItem(itemDto, 1L));
        verify(requestRepository).findById(1L);
    }

    @Test
    void shouldGetItemByOwner() {
        Booking last = Booking.builder().id(1L).item(item).booker(user).status(Status.APPROVED).build();
        Booking next = Booking.builder().id(2L).item(item).booker(user).status(Status.APPROVED).build();
        Comment comment = Comment.builder().item(item).author(user).text("test").created(LocalDateTime.now()).build();
        when(commentRepository.findByItem_IdOrderByCreatedDesc(1L)).thenReturn(Set.of(comment));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.getFirstByItem_IdOrderByEndDesc(1L)).thenReturn(next);
        when(bookingRepository.getFirstByItem_IdOrderByStartAsc(1L)).thenReturn(last);
        itemDto.setComments(Set.of(CommentMapper.toCommentDto(comment)));
        itemDto.setNextBooking(BookingMapper.toBookingDtoItem(next));
        itemDto.setLastBooking(BookingMapper.toBookingDtoItem(last));
        ItemDto result = itemService.getItemByOwner(1L, 1L);
        assertThat(result).isNotNull().isEqualTo(itemDto);
        verify(itemRepository).findById(1L);
        verify(commentRepository).findByItem_IdOrderByCreatedDesc(1L);
        verify(bookingRepository).getFirstByItem_IdOrderByEndDesc(1L);
        verify(bookingRepository).getFirstByItem_IdOrderByStartAsc(1L);
    }

    @Test
    void shouldGetItemByOwnerWithIncorrectId() {
        when(itemRepository.findById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemService.getItemByOwner(1L, 1L));
        verify(itemRepository).findById(1L);
    }

    @Test
    void shouldGetUsersItems() {
        try (MockedStatic<CustomPageable> ignored = mockStatic(CustomPageable.class)) {
            Pageable pageable = CustomPageable.of(0, 5);
            when(CustomPageable.of(0, 5)).thenReturn(pageable);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            Page<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
            when(itemRepository.findAllByOwner(user, pageable)).thenReturn(itemPage);
            Collection<ItemDto> result = itemService.getUserItems(1L, 0, 5);
            assertThat(result.isEmpty()).isNotNull().isFalse();
            verify(itemRepository).findAllByOwner(user, pageable);
        }
    }

    @Test
    void shouldUpdateItem() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        itemDto.setName("new");
        itemDto.setDescription("new");
        itemDto.setAvailable(false);
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        when(itemRepository.save(item)).thenReturn(item);
        ItemDto result = itemService.updateItem(itemDto, 1L, 1L);
        assertThat(result).isNotNull().isEqualTo(itemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void shouldUpdateItemIfUserNotOwner() {
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.updateItem(itemDto, 1L, 2L));
    }

    @Test
    void shouldSearchItems() {
        try (MockedStatic<CustomPageable> ignored = mockStatic(CustomPageable.class)) {
            Pageable pageable = CustomPageable.of(0, 5);
            when(CustomPageable.of(0, 5)).thenReturn(pageable);
            Page<Item> itemPage = new PageImpl<>(Collections.singletonList(item));
            when(itemRepository.searchItems("item", pageable)).thenReturn(itemPage);
            List<ItemDto> result = new ArrayList<>(itemService.searchItems("item", 0, 5));
            assertThat(result.get(0)).isNotNull().isEqualTo(itemDto);
            verify(itemRepository).searchItems("item", pageable);
        }
    }

    @Test
    void shouldCreateComment() {
        try (MockedStatic<LocalDateTime> ignored = mockStatic(LocalDateTime.class)) {
            Comment comment = Comment.builder().id(1L).text("text").author(user).item(item).build();
            CommentDto commentDto = CommentMapper.toCommentDto(comment);
            when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            LocalDateTime dateTime = LocalDateTime.now();
            when(LocalDateTime.now()).thenReturn(dateTime);
            when(bookingRepository.existsAllByBooker_IdAndEndBefore(1L, dateTime)).thenReturn(true);
            when(commentRepository.save(Mockito.any())).thenReturn(comment);
            CommentDto result = itemService.createComment(commentDto, 1L, 1L);
            assertThat(result).isNotNull().isEqualTo(commentDto);
            verify(commentRepository).save(Mockito.any());
        }
    }

    @Test
    void shouldCreateCommentIfUserDontBooking() {
        /*Comment comment = Comment.builder().id(1L).text("text").author(user).item(item).build();
        CommentDto commentDto = CommentMapper.toCommentDto(comment);
        Clock clock = Clock.fixed(Instant.parse("2022-12-12T10:15:30.00Z"), ZoneId.of("UTC"));
        LocalDateTime dateTime = LocalDateTime.now(clock);
        mockStatic(LocalDateTime.class);
        when(LocalDateTime.now()).thenReturn(dateTime);
        when(bookingRepository.existsAllByBooker_IdAndEndBefore(1L, dateTime)).thenReturn(false);
        assertThrows(BadRequestException.class, () -> itemService.createComment(commentDto, 1L, 1L));
        verify(bookingRepository).existsAllByBooker_IdAndEndBefore(1L, dateTime);
        mockStatic(LocalDateTime.class).closeOnDemand();*/
        try (MockedStatic<LocalDateTime> ignored = mockStatic(LocalDateTime.class)) {
            Comment comment = Comment.builder().id(1L).text("text").author(user).item(item).build();
            CommentDto commentDto = CommentMapper.toCommentDto(comment);
            LocalDateTime dateTime = LocalDateTime.now();
            when(LocalDateTime.now()).thenReturn(dateTime);
            when(bookingRepository.existsAllByBooker_IdAndEndBefore(1L, dateTime)).thenReturn(false);
            assertThrows(BadRequestException.class, () -> itemService.createComment(commentDto, 1L, 1L));
            verify(bookingRepository).existsAllByBooker_IdAndEndBefore(1L, dateTime);
        }
    }
}
