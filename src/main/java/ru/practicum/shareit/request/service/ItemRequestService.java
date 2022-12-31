package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;

    private User checkAndReturnUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id: " + userId + " не найден"));
    }

    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Long requesterId) {
        ItemRequest itemRequest = ItemRequestMapper.fromItemRequestDto(itemRequestDto, checkAndReturnUser(requesterId));
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    public ItemRequestDto getItemRequest(Long requestId, Long requesterId) {
        checkAndReturnUser(requesterId);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с id:" + requestId + " не найден")));
    }

    public Collection<ItemRequestDto> getByUser(Long userId) {
        checkAndReturnUser(userId);
        return itemRequestRepository.findAllByRequesterId(userId).stream().map(ItemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    public Collection<ItemRequestDto> getAllByUserId(Long userId, Pageable pageable) {
        checkAndReturnUser(userId);
        return itemRequestRepository.findAllByRequesterIdIsNot(userId, pageable).stream()
                .map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }
}
