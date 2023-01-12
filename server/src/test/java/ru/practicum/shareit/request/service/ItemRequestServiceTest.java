package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.CustomPageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @InjectMocks
    private ItemRequestService itemRequestService;
    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("user").email("user@user.ru").build();
        itemRequest = ItemRequest.builder().items(new ArrayList<>()).id(1L).requester(user)
                .created(LocalDateTime.now()).description("test").build();
        itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    @Test
    void shouldCreateItemRequest() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);
        ItemRequestDto result = itemRequestService.createItemRequest(itemRequestDto, anyLong());
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(itemRequestDto);
        verify(itemRequestRepository).save(any());
    }

    @Test
    void shouldCreateItemRequestWithIncorrectId() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.createItemRequest(itemRequestDto, anyLong()));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void shouldGetItemRequest() {
        itemRequest.setItems(null);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        ItemRequestDto result = itemRequestService.getItemRequest(1L, anyLong());
        assertThat(result).isNotNull().isEqualTo(itemRequestDto);
        verify(itemRequestRepository).findById(1L);
    }

    @Test
    void shouldGetItemRequestWithIncorrectId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getItemRequest(1L, anyLong()));
        verify(itemRequestRepository).findById(1L);
    }

    @Test
    void shouldGetByUserId() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterId(1L)).thenReturn(List.of(itemRequest));
        List<ItemRequestDto> result = new ArrayList<>(itemRequestService.getByUser(1L));
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(itemRequestDto);
        verify(itemRequestRepository).findAllByRequesterId(1L);
    }

    @Test
    void shouldGetByIncorrectUserId() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getByUser(anyLong()));
        verify(userRepository).findById(anyLong());
    }

    @Test
    void shouldGetAllByUserId() {
        try (MockedStatic<CustomPageable> ignored = mockStatic(CustomPageable.class)) {
            Pageable pageable = CustomPageable.of(0, 5);
            when(CustomPageable.of(0, 5)).thenReturn(pageable);
            Page<ItemRequest> itemPage = new PageImpl<>(Collections.singletonList(itemRequest));
            when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
            when(itemRequestRepository.findAllByRequesterIdIsNot(1L, pageable)).thenReturn(itemPage);
            List<ItemRequestDto> result = new ArrayList<>(itemRequestService.getAllByUserId(1L,
                    CustomPageable.of(0, 5)));
            assertThat(result.isEmpty()).isFalse();
            assertThat(result.get(0)).isEqualTo(itemRequestDto);
            verify(itemRequestRepository).findAllByRequesterIdIsNot(1L, pageable);
        }
    }

    @Test
    void shouldGetAllWithIncorrectId() {
        when(userRepository.findById(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> itemRequestService.getAllByUserId(anyLong(),
                CustomPageable.of(0, 5)));
        verify(userRepository).findById(anyLong());
    }
}
