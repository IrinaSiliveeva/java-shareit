package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;

    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userRepository.findById(ownerId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + ownerId + " не найден")));
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = requestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() ->
                            new NotFoundException("Запрос с таким id: " + itemDto.getRequestId() + "не найден"));
            item.setRequest(itemRequest);
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public ItemDto getItemByOwner(Long itemId, Long ownerId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        setComments(itemDto);
        if (item.getOwner().getId().equals(ownerId)) {
            setBooking(itemDto, itemId);
        }
        return itemDto;
    }

    private void setComments(ItemDto itemDto) {
        itemDto.setComments(commentRepository.findByItem_IdOrderByCreatedDesc(itemDto.getId()).stream()
                .map(CommentMapper::toCommentDto).collect(Collectors.toSet()));
    }

    private void setBooking(ItemDto itemDto, Long itemId) {
        Booking last = bookingRepository.getFirstByItem_IdOrderByStartAsc(itemId);
        if (last != null) {
            itemDto.setLastBooking(BookingMapper.toBookingDtoItem(last));
        }
        Booking next = bookingRepository.getFirstByItem_IdOrderByEndDesc(itemId);
        if (next != null) {
            itemDto.setNextBooking(BookingMapper.toBookingDtoItem(next));
        }
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id " + itemId + " не найден"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id " + userId + " не найден"));
    }

    public Collection<ItemDto> getUserItems(Long userId, Integer from, Integer size) {
        User user = getUser(userId);
        Pageable pageable = CustomPageable.of(from, size);
        Collection<ItemDto> items = itemRepository.findAllByOwner(user, pageable).stream()
                .filter((Item x) -> Objects.equals(userId, x.getOwner().getId())).sorted(Comparator.comparing(Item::getId))
                .map(ItemMapper::toItemDto).collect(Collectors.toList());
        for (ItemDto itemDto : items) {
            setComments(itemDto);
            setBooking(itemDto, itemDto.getId());
        }
        return items;
    }

    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        Item item = getItem(itemId);
        if (!Objects.equals(item.getOwner().getId(), ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не является владельцем вещи");
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    public Collection<ItemDto> searchItems(String keyword, Integer from, Integer size) {
        if (keyword.isEmpty()) {
            return new ArrayList<>();
        }
        Pageable pageable = CustomPageable.of(from, size);
        return itemRepository.searchItems(keyword, pageable).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        if (!bookingRepository.existsAllByBooker_IdAndEndBefore(userId, LocalDateTime.now())) {
            throw new BadRequestException("Пользователь не бронировал этот предмет");
        }
        User author = getUser(userId);
        Item item = getItem(itemId);
        Comment comment = CommentMapper.fromCommentDto(commentDto, author, item);
        comment.setCreated(LocalDateTime.now());
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}