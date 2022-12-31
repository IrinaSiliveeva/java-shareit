package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> itemDtoJacksonTester;
    private Validator validator;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        itemRequestDto = ItemRequestDto.builder()
                .items(new ArrayList<>())
                .requesterId(1L)
                .id(1L)
                .created(LocalDateTime.now())
                .build();
    }

    @Test
    void shouldCastToJson() throws IOException {
        JsonContent<ItemRequestDto> result = itemDtoJacksonTester.write(itemRequestDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
    }

    @Test
    void shouldCreateWithEmptyDescription() {
        itemRequestDto.setDescription("");
        Set<ConstraintViolation<ItemRequestDto>> result = validator.validate(itemRequestDto);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateWithNullDescription() {
        itemRequestDto.setDescription(null);
        Set<ConstraintViolation<ItemRequestDto>> result = validator.validate(itemRequestDto);
        assertThat(result).isNotEmpty();
    }
}
