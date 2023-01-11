package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.Create;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> itemDtoJacksonTester;
    private Validator validator;
    private ItemDto itemDto;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        itemDto = ItemDto.builder().id(1L).name("item").description("test").available(true).build();
    }

    @Test
    void shouldCastToJson() throws IOException {
        JsonContent<ItemDto> result = itemDtoJacksonTester.write(itemDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo(itemDto.getName());
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo(itemDto.getDescription());
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
    }

    @Test
    void shouldCreateItemWithEmptyName() {
        itemDto.setName("");
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateItemWithNullName() {
        itemDto.setName(null);
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateItemWithEmptyDescription() {
        itemDto.setDescription("");
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateItemWithNullDescription() {
        itemDto.setDescription(null);
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateWithNullAvailable() {
        itemDto.setAvailable(null);
        Set<ConstraintViolation<ItemDto>> result = validator.validate(itemDto, Create.class);
        assertThat(result).isNotEmpty();
    }
}
