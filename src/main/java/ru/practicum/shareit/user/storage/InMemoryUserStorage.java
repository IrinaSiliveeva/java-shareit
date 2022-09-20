package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage {
    private final Map<Long, User> userStorage = new HashMap<>();
    private long id = 1;

    private void checkEmail(User user) {
        for (User checkUser : userStorage.values()) {
            if (checkUser.getEmail().equals(user.getEmail())) {
                throw new ConflictException("Пользователь с таким email уже существует");
            }
        }
    }

    public User save(User user) {
        checkEmail(user);
        if (user.getId() == 0) {
            user.setId(id++);
        }
        userStorage.put(user.getId(), user);
        return user;
    }

    public Collection<User> getAllUsers() {
        return userStorage.values();
    }

    public void checkUser(Long id) {
        if (!userStorage.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
    }

    public User getUser(Long id) {
        checkUser(id);
        return userStorage.get(id);
    }

    public User deleteUser(Long id) {
        checkUser(id);
        return userStorage.remove(id);
    }

    public User updateUser(User updateUser, Long id) {
        checkEmail(updateUser);
        checkUser(id);
        User user = userStorage.get(id);
        if (updateUser.getEmail() != null) {
            user.setEmail(updateUser.getEmail());
        }
        if (updateUser.getName() != null) {
            user.setName(updateUser.getName());
        }
        return user;
    }
}