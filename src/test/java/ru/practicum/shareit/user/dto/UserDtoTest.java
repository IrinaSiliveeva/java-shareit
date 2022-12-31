package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.Create;

import javax.validation.*;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class UserDtoTest {
    @Autowired
    JacksonTester<UserDto> userDtoJacksonTester;
    private Validator validator;
    private UserDto userDto;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        userDto = UserDto.builder().id(1L).name("test").email("test@test.ru").build();
    }

    @Test
    void shouldGetJsonFromDto() throws Exception {
        JsonContent<UserDto> result = userDtoJacksonTester.write(userDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(userDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.email").isEqualTo(userDto.getEmail());
    }

    @Test
    void shouldCreateUserIfNameIsEmpty() {
        userDto.setName("");
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateUserIfNameIsNull() {
        userDto.setName(null);
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateUserIfEmailIsEmpty() {
        userDto.setEmail("");
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateUserIfEmailIsNull() {
        userDto.setEmail(null);
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateUserIfEmailNotValid() {
        userDto.setEmail("abc");
        Set<ConstraintViolation<UserDto>> result = validator.validate(userDto, Create.class);
        assertThat(result).isNotEmpty();
    }
}
