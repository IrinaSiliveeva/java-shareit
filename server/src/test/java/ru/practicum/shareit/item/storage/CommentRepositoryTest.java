package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    private Comment comment;
    private Item item;

    @BeforeEach
    void setup() {
        User user = User.builder().name("user").email("test@test.ru").build();
        user = userRepository.save(user);
        item = Item.builder()
                .id(1L)
                .owner(user)
                .name("item")
                .description("test")
                .build();
        item = itemRepository.save(item);
        comment = Comment.builder().text("text").author(user).item(item).build();
        comment = commentRepository.save(comment);
    }

    @Test
    void shouldFindCommentByItemId() {
        List<Comment> result = commentRepository.findByItem_IdOrderByCreatedDesc(item.getId())
                .stream().collect(Collectors.toList());
        assertThat(result).isNotNull();
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.get(0)).isEqualTo(comment);
    }

    @Test
    void shouldFindCommentByIncorrectId() {
        Set<Comment> result = commentRepository.findByItem_IdOrderByCreatedDesc(404L);
        assertThat(result.isEmpty()).isTrue();
    }
}
