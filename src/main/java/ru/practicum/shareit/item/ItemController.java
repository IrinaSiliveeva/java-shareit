package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                              @RequestBody @Validated({Create.class}) ItemDto itemDto) {
        return itemService.createItem(itemDto, ownerId);
    }

    @GetMapping("{id}")
    public ItemDto getItem(@PathVariable Long id) {
        return itemService.getItem(id);
    }

    @GetMapping
    public Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        return itemService.getUserItems(ownerId);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId,
                              @RequestBody ItemDto itemDto, @PathVariable Long id) {
        return itemService.updateItem(itemDto, id, ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text", defaultValue = "") String keyword) {
        return itemService.searchItems(keyword);
    }
}