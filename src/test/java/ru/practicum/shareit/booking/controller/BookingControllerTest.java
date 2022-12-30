package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    private BookingService bookingService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    private final String url = "/bookings/";
    private final String header = "X-Sharer-User-Id";
    private BookingDtoInput bookingDtoInput;
    private BookingDtoOutput bookingDtoOutput;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        User user = User.builder().id(1L).name("user").email("user@user.ru").build();
        Item item = Item.builder().owner(user).available(true).name("item").description("test").build();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        Booking booking = Booking.builder().id(1L).start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(10)).status(Status.WAITING).item(item).booker(user).build();
        bookingDtoOutput = BookingMapper.toBookingDtoOutput(booking);
        bookingDtoInput = BookingDtoInput.builder().itemId(1L).start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(10)).build();
    }

    @Test
    void shouldCreateBooking() throws Exception {
        when(bookingService.createBooking(bookingDtoInput, 1L)).thenReturn(bookingDtoOutput);
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class));
        verify(bookingService).createBooking(bookingDtoInput, 1L);
    }

    @Test
    void shouldCreateBookingWithIncorrectId() throws Exception {
        when(bookingService.createBooking(bookingDtoInput, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(bookingDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(NotFoundException.class)));
        verify(bookingService).createBooking(bookingDtoInput, 1L);
    }

    @Test
    void shouldGetBookingById() throws Exception {
        when(bookingService.getBooking(1L, 1L)).thenReturn(bookingDtoOutput);
        mockMvc.perform(get(url + "{id}", 1)
                        .header(header, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoOutput.getId()), Long.class));
        verify(bookingService).getBooking(1L, 1L);
    }

    @Test
    void shouldGetBookingWithIncorrectId() throws Exception {
        when(bookingService.getBooking(1L, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(get(url + "{id}", 1)
                        .header(header, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(NotFoundException.class)));
        verify(bookingService).getBooking(1L, 1L);
    }

    @Test
    void shouldApproveBooking() throws Exception {
        bookingDtoOutput.setStatus(Status.APPROVED);
        when(bookingService.approveBooking(1L, 1L, true)).thenReturn(bookingDtoOutput);
        mockMvc.perform(patch(url + "{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("approved", String.valueOf(true))
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("APPROVED")));
        verify(bookingService).approveBooking(1L, 1L, true);
    }

    @Test
    void shouldGetAllBookingByUserId() throws Exception {
        when(bookingService.getAllBookingByUser(1L, "FUTURE", 0, 5))
                .thenReturn(Collections.singleton(bookingDtoOutput));
        mockMvc.perform(get(url)
                        .header(header, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
        verify(bookingService).getAllBookingByUser(1L, "FUTURE", 0, 5);
    }

    @Test
    void shouldGetAllBookingByOwner() throws Exception {
        when(bookingService.getAllBookingByOwner(1L, "FUTURE", 0, 5))
                .thenReturn(Collections.singleton(bookingDtoOutput));
        mockMvc.perform(get(url + "owner")
                        .header(header, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "FUTURE")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
        verify(bookingService).getAllBookingByOwner(1L, "FUTURE", 0, 5);
    }

    @Test
    void shouldGetAllBookingByOwnerUnknownState() throws Exception {
        when(bookingService.getAllBookingByOwner(1L, "abc", 0, 5))
                .thenThrow(BadRequestException.class);
        mockMvc.perform(get(url + "owner")
                        .header(header, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", "abc")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(BadRequestException.class)));
    }
}
