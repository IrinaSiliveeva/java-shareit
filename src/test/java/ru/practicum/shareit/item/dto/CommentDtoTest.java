package ru.practicum.shareit.item.dto;

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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> commentDtoJacksonTester;
    private Validator validator;
    private CommentDto commentDto;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        commentDto = CommentDto.builder().id(1L).created(LocalDateTime.now())
                .text("test").authorName("user").build();
    }

    @Test
    void shouldCastCommentToJson() throws IOException {
        JsonContent<CommentDto> result = commentDtoJacksonTester.write(commentDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.text").isEqualTo("test");
    }

    @Test
    void shouldCreateCommentWithEmptyText() {
        commentDto.setText("");
        Set<ConstraintViolation<CommentDto>> result = validator.validate(commentDto);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldCreateCommentWithNullText() {
        commentDto.setText(null);
        Set<ConstraintViolation<CommentDto>> result = validator.validate(commentDto);
        assertThat(result).isNotEmpty();
    }
}
