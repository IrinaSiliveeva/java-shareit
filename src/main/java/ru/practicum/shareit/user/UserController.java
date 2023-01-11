package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Create;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated({Create.class}) @RequestBody UserDto userDto) {
        log.info("создан пользователь: " + userDto);
        return userService.createUser(userDto);
    }

    @GetMapping
    public Collection<UserDto> getAllUsers() {
        log.info("получение списка всех пользователей");
        return userService.getAllUsers();
    }

    @GetMapping("{id}")
    public UserDto getUser(@PathVariable Long id) {
        log.info("получение пользователя под id: " + id);
        return userService.getUser(id);
    }

    @DeleteMapping("{id}")
    public UserDto deleteUser(@PathVariable Long id) {
        log.info("удаление пользователя под id: " + id);
        return userService.deleteUser(id);
    }

    @PatchMapping("{id}")
    public UserDto updateUser(@RequestBody UserDto userDto, @PathVariable Long id) {
        log.info("обновление информации:" + userDto + " под id: " + id);
        return userService.updateUser(userDto, id);
    }
}