package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @RequestBody @Validated({Create.class}) ItemDto itemDto) {
        log.info("пользователь под id: " + ownerId + " создал вещь: " + itemDto);
        return itemService.createItem(itemDto, ownerId);
    }

    @GetMapping("{id}")
    public ItemDto getItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                           @PathVariable Long id) {
        log.info("пользователь под id: " + ownerId + " получил вещь под id: " + id);
        return itemService.getItemByOwner(id, ownerId);
    }

    @GetMapping
    public Collection<ItemDto> getUserItems(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                            @RequestParam(name = "from", required = false) Integer from,
                                            @RequestParam(name = "size", required = false) Integer size) {
        log.info("пользователь под id: " + ownerId + " получил список своих вещей");
        return itemService.getUserItems(ownerId, from, size);
    }

    @PatchMapping("{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @RequestBody ItemDto itemDto, @PathVariable Long id) {
        log.info("пользователь под id: " + ownerId + " внес изменения: " + itemDto + " в вещь под id: " + id);
        return itemService.updateItem(itemDto, id, ownerId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItems(@RequestParam(name = "text", defaultValue = "") String keyword,
                                           @RequestParam(name = "from", required = false) Integer from,
                                           @RequestParam(name = "size", required = false) Integer size) {
        log.info("поиск вещи по фразе: " + keyword);
        return itemService.searchItems(keyword, from, size);
    }

    @PostMapping("{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId) {
        log.info("пользователь под id: " + userId + " оставил комментарий: " + commentDto +
                " для вещи под id " + itemId);
        return itemService.createComment(commentDto, itemId, userId);
    }
}