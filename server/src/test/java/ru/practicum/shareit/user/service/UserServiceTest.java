package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserService userService;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        user = User.builder().id(1L).name("test").email("test@test.ru").build();
        userDto = UserMapper.toUserDto(user);
    }

    @Test
    void shouldCreateUser() {
        when(userRepository.save(user)).thenReturn(user);
        assertThat(userService.createUser(userDto)).isEqualTo(UserMapper.toUserDto(user));
        verify(userRepository).save(user);
    }

    @Test
    void shouldCreateUserWithNullEmail() {
        userDto.setEmail(null);
        assertThrows(BadRequestException.class, () -> userService.createUser(userDto));
    }

    @Test
    void shouldGetUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        assertThat(userService.getUser(1L)).isEqualTo(userDto);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldGetUserWithIncorrectId() {
        when(userRepository.findById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.getUser(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserDto> result = new ArrayList<>(userService.getAllUsers());
        assertThat(result.isEmpty()).isFalse();
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.equals(List.of(userDto))).isTrue();
        verify(userRepository).findAll();
    }

    @Test
    void shouldUpdateUser() {
        user.setName("new");
        userDto.setName("new");
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(user);
        assertThat(userService.updateUser(userDto, 1L).getName()).isEqualTo("new");
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    void shouldUpdateUserWithIncorrectId() {
        when(userRepository.findById(1L)).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.updateUser(userDto, 1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldUpdateUserIfEmailIsExist() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);
        assertThrows(ConflictException.class, () -> userService.updateUser(userDto, 1L));
        verify(userRepository).existsByEmail(user.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        assertThat(userService.deleteUser(1L)).isEqualTo(userDto);
        verify(userRepository).deleteById(1L);
    }
}
