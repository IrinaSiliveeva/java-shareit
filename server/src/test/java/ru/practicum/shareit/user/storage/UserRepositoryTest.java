package ru.practicum.shareit.user.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("test").email("test@test.ru").build();
    }

    @Test
    void shouldCheckEmail() {
        userRepository.save(user);
        assertThat(userRepository.existsByEmail("test@test.ru")).isTrue();
        assertThat(userRepository.existsByEmail("abc@abc.com")).isFalse();
    }
}
