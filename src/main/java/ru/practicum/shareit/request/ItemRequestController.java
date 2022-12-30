package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.CustomPageable;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @GetMapping("{requestId}")
    public ItemRequestDto getItemRequestById(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                             @PathVariable Long requestId) {
        return itemRequestService.getItemRequest(requestId, requesterId);
    }

    @GetMapping
    public Collection<ItemRequestDto> getByUser(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId) {
        return itemRequestService.getByUser(requesterId);
    }

    @GetMapping("all")
    public Collection<ItemRequestDto> getAll(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                             @RequestParam(name = "from", required = false) Integer from,
                                             @RequestParam(name = "size", required = false) Integer size) {
        return itemRequestService.getAllByUserId(requesterId, CustomPageable.of(from, size));
    }

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader("X-Sharer-User-Id") @NotNull Long requesterId,
                                            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return itemRequestService.createItemRequest(itemRequestDto, requesterId);
    }
}
