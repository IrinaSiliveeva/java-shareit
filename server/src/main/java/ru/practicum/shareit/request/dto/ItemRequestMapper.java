package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ItemRequestMapper {

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        if (itemRequest.getItems() != null) {
            return ItemRequestDto.builder()
                    .id(itemRequest.getId())
                    .description(itemRequest.getDescription())
                    .created(itemRequest.getCreated())
                    .items(itemRequest.getItems().stream().map(ItemMapper::toItemDto).collect(Collectors.toList()))
                    .build();
        } else {
            return ItemRequestDto.builder()
                    .id(itemRequest.getId())
                    .description(itemRequest.getDescription())
                    .created(itemRequest.getCreated())
                    .items(new ArrayList<>())
                    .build();
        }
    }

    public static ItemRequest fromItemRequestDto(ItemRequestDto itemRequestDto, User requester) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .created(itemRequestDto.getCreated())
                .build();
    }
}
