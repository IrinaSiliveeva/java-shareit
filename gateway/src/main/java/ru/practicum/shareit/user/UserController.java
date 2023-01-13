package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Create;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@RequestBody @Validated({Create.class}) UserDto userDto) {
        log.info("Создан пользователь {}", userDto);
        return userClient.createUser(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Получение списка всех пользователей");
        return userClient.getAllUsers();
    }

    @GetMapping("{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        log.info("Получение пользователя, пользователь={}", id);
        return userClient.getUser(id);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        log.info("Удаление пользователя, пользователь={}", id);
        return userClient.deleteUser(id);
    }

    @PatchMapping("{id}")
    public ResponseEntity<Object> updateUser(@PathVariable Long id,
                                             @RequestBody UserDto userDto) {
        log.info("Обновление пользователя {}, пользователь={}", userDto, id);
        return userClient.updateUser(userDto, id);
    }
}
