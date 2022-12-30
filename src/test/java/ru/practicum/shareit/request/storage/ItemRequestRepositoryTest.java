package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.CustomPageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private ItemRequest itemRequest;

    @BeforeEach
    void setup() {
        user = User.builder().name("user").email("user@user.ru").build();
        user = userRepository.save(user);
        itemRequest = ItemRequest.builder()
                .items(new ArrayList<>())
                .requester(user)
                .description("test")
                .created(LocalDateTime.now())
                .build();
        itemRequest = itemRequestRepository.save(itemRequest);
    }


    @Test
    void shouldFindAllByRequesterId() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequesterId(user.getId());
        assertThat(result).isNotNull();
        assertThat(result.get(0)).isEqualTo(itemRequest);
    }

    @Test
    void shouldFindAllByIncorrectRequesterId() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequesterId(404L);
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void shouldFindAllByRequesterIdIsNot() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequesterIdIsNot(40L,
                CustomPageable.of(0, 5)).stream().collect(Collectors.toList());
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(itemRequest);
    }

    @Test
    void shouldFindAllByIncorrectRequesterIdIsNot() {
        List<ItemRequest> result = itemRequestRepository.findAllByRequesterIdIsNot(user.getId(),
                CustomPageable.of(0, 5)).stream().collect(Collectors.toList());
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isTrue();
    }
}
