package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage {
    private final Map<Long, Item> itemStorage = new HashMap<>();
    private long id = 1;

    public Item getItem(Long id) {
        if (!itemStorage.containsKey(id)) {
            throw new NotFoundException("Предмет с id " + id + " не найден");
        }
        return itemStorage.get(id);
    }

    public Collection<Item> getUserItems(Long ownerId) {
        return itemStorage.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId)).collect(Collectors.toList());
    }

    public Item save(Item item) {
        if (itemStorage.containsKey(item.getId())) {
            throw new ConflictException("Предмет с id " + item.getId() + " уже существует");
        }
        if (item.getId() == null) {
            item.setId(id++);
        }
        itemStorage.put(item.getId(), item);
        return item;
    }

    public Item deleteItem(Long id) {
        if (!itemStorage.containsKey(id)) {
            throw new NotFoundException("Предмет с id " + id + " не найден");
        }
        return itemStorage.remove(id);
    }

    public Collection<Item> search(String keyword) {
        if (keyword.isEmpty()) {
            return new ArrayList<>();
        }
        return itemStorage.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase())
                        || item.getDescription().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Item updateItem(Item updateItem, Long itemId, Long ownerId) {
        Item item = getItem(itemId);
        if (!item.getOwnerId().equals(ownerId)) {
            throw new NotFoundException("Предмет пользователя с id " + ownerId + " не найден");
        }
        if (updateItem.getName() != null) {
            item.setName(updateItem.getName());
        }
        if (updateItem.getDescription() != null) {
            item.setDescription(updateItem.getDescription());
        }
        if (updateItem.getAvailable() != null) {
            item.setAvailable(updateItem.getAvailable());
        }
        return item;
    }
}