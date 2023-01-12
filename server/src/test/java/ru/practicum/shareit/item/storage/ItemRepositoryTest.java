package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;
import ru.practicum.shareit.util.CustomPageable;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    private Item item;
    private User user;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        user = User.builder().name("user").email("test@test.ru").build();
        user = userRepository.save(user);
        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("item")
                .description("test")
                .build();
        item = itemRepository.save(item);
    }

    @Test
    void shouldFindItem() {
        List<Item> result = itemRepository.searchItems("item", CustomPageable.of(0, 5))
                .stream().collect(Collectors.toList());
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(item);
    }

    @Test
    void shouldFindItemWithWrongText() {
        List<Item> result = itemRepository.searchItems("abc", CustomPageable.of(0, 5))
                .stream().collect(Collectors.toList());
        assertThat(result.isEmpty()).isTrue();
    }

    @Test
    void shouldFindAllByOwner() {
        List<Item> result = itemRepository.findAllByOwner(user, CustomPageable.of(0, 5))
                .stream().collect(Collectors.toList());
        assertThat(result).isNotNull();
        assertThat(result.get(0)).isEqualTo(item);
    }
}
