package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoInputTest {
    private Validator validator;
    private BookingDtoInput bookingDtoInput;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        bookingDtoInput = BookingDtoInput.builder().itemId(1L).start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1)).build();
    }

    @Test
    void shouldValidIfItemIdIsNull() {
        bookingDtoInput.setItemId(null);
        Set<ConstraintViolation<BookingDtoInput>> result = validator.validate(bookingDtoInput);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldValidIfStartTimeIsNull() {
        bookingDtoInput.setStart(null);
        Set<ConstraintViolation<BookingDtoInput>> result = validator.validate(bookingDtoInput);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldValidIfStartTimeInPast() {
        bookingDtoInput.setStart(LocalDateTime.now().minusDays(10));
        Set<ConstraintViolation<BookingDtoInput>> result = validator.validate(bookingDtoInput);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldValidIfEndTimeIsNull() {
        bookingDtoInput.setEnd(null);
        Set<ConstraintViolation<BookingDtoInput>> result = validator.validate(bookingDtoInput);
        assertThat(result).isNotEmpty();
    }

    @Test
    void shouldValidIfEndTimeInPast() {
        bookingDtoInput.setEnd(LocalDateTime.now().minusDays(10));
        Set<ConstraintViolation<BookingDtoInput>> result = validator.validate(bookingDtoInput);
        assertThat(result).isNotEmpty();
    }
}
