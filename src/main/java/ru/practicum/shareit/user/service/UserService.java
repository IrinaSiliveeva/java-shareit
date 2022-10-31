package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.InMemoryUserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage userStorage;

    public Collection<UserDto> getAllUsers() {
        List<UserDto> allUsers = new ArrayList<>();
        for (User user : userStorage.getAllUsers()) {
            allUsers.add(UserMapper.toUserDto(user));
        }
        return allUsers;
    }

    public UserDto getUser(Long id) {
        return UserMapper.toUserDto(userStorage.getUser(id));
    }

    public UserDto createUser(UserDto userDto) {
        return UserMapper.toUserDto(userStorage.save(UserMapper.fromUserDto(userDto)));
    }

    public UserDto updateUser(UserDto userDto, Long id) {
        return UserMapper.toUserDto(userStorage.updateUser(UserMapper.fromUserDto(userDto), id));
    }

    public UserDto deleteUser(Long id) {
        return UserMapper.toUserDto(userStorage.deleteUser(id));
    }
}