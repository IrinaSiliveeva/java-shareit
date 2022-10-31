package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.InMemoryItemStorage;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final InMemoryItemStorage itemStorage;
    private final InMemoryUserStorage userStorage;

    public ItemDto createItem(ItemDto itemDto, Long ownerId) {
        userStorage.checkUser(ownerId);
        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwnerId(ownerId);
        return ItemMapper.toItemDto(itemStorage.save(item));
    }

    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    public Collection<ItemDto> getUserItems(Long ownerId) {
        userStorage.checkUser(ownerId);
        List<ItemDto> itemsList = new ArrayList<>();
        for (Item item : itemStorage.getUserItems(ownerId)) {
            itemsList.add(ItemMapper.toItemDto(item));
        }
        return itemsList;
    }

    public ItemDto updateItem(ItemDto itemDto, Long itemId, Long ownerId) {
        if (!itemStorage.getItem(itemId).getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Пользователь с id " + ownerId + " не является владельцем");
        }
        return ItemMapper.toItemDto(itemStorage.updateItem(ItemMapper.fromItemDto(itemDto), itemId, ownerId));
    }

    public Collection<ItemDto> searchItems(String keyword) {
        List<ItemDto> itemsList = new ArrayList<>();
        for (Item item : itemStorage.search(keyword)) {
            itemsList.add(ItemMapper.toItemDto(item));
        }
        return itemsList;
    }
}