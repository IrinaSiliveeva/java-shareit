package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.CustomPageable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                             @PathVariable Long requestId) {
        log.info("получение пользователем под id: " + requesterId + " запроса под id: " + requestId);
        return itemRequestService.getItemRequest(requestId, requesterId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getByUser(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId) {
        log.info("получение списка запросов по пользователю под id: " + requesterId);
        return itemRequestService.getByUser(requesterId);
    }

    @GetMapping("all")
    public Collection<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                             @PositiveOrZero @RequestParam(name = "from", required = false) Integer from,
                                             @Positive @RequestParam(name = "size", required = false) Integer size) {
        log.info("пользователь под id: " + requesterId + " получил список своих запросов");
        return itemRequestService.getAllByUserId(requesterId, CustomPageable.of(from, size));
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        log.info("пользователь под id: " + requesterId + " создал запрос на: " + itemRequestDto);
        return itemRequestService.createItemRequest(itemRequestDto, requesterId);
    }
}
