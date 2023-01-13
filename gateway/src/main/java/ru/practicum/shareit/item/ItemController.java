package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.client.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Create;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Validated({Create.class}) @RequestBody ItemDto itemDto) {
        log.info("Пользователь создал предмет {},пользователь={}", itemDto, userId);
        return itemClient.createItem(userId, itemDto);
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                          @PathVariable Long id) {
        log.info("Пользотель получил предмет, пользователь={}, предмет={}", userId, id);
        return itemClient.getItem(userId, id);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                               @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Пользователь получил список всех своих предметов, пользователь={}, от={}, размер{}", userId, from, size);
        return itemClient.getUserItems(userId, from, size);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PathVariable Long id,
                                             @RequestBody ItemDto itemDto) {
        log.info("Пользователь обновил предмет {}, пользователь={}, предмет={}", itemDto, userId, id);
        return itemClient.updateItem(id, userId, itemDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam(name = "text") String text,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("Поиск предмета, запрос={}, от={}, размер={}", text, from, size);
        return itemClient.searchItems(text, from, size);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @PathVariable Long id,
                                                @Valid @RequestBody CommentDto commentDto) {
        log.info("Пользователь создал коментарий {}, пользователь={}, предмет={}", commentDto, userId, id);
        return itemClient.createComment(commentDto, id, userId);
    }
}
