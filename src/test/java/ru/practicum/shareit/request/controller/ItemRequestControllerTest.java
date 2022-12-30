package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.CustomPageable;

import java.util.Collections;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    private ItemRequestService itemRequestService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private ItemRequestDto itemRequestDto;
    private final String url = "/requests";
    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        itemRequestDto = ItemRequestDto.builder().id(1L)
                .requesterId(1L).description("test").build();
    }

    @Test
    void shouldCreateItemRequest() throws Exception {
        when(itemRequestService.createItemRequest(itemRequestDto, 1L)).thenReturn(itemRequestDto);
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
        verify(itemRequestService).createItemRequest(itemRequestDto, 1L);
    }

    @Test
    void shouldCreateItemRequestWithIncorrectUser() throws Exception {
        when(itemRequestService.createItemRequest(itemRequestDto, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(NotFoundException.class)));
        verify(itemRequestService).createItemRequest(itemRequestDto, 1L);
    }

    @Test
    void shouldGetItemRequestById() throws Exception {
        when(itemRequestService.getItemRequest(1L, 1L)).thenReturn(itemRequestDto);
        mockMvc.perform(get(url + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
        verify(itemRequestService).getItemRequest(1L, 1L);
    }

    @Test
    void shouldGetItemRequestWithIncorrectUserId() throws Exception {
        when(itemRequestService.getItemRequest(1L, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(get(url + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(NotFoundException.class)));
        verify(itemRequestService).getItemRequest(1L, 1L);
    }

    @Test
    void shouldGetByUser() throws Exception {
        when(itemRequestService.getByUser(1L)).thenReturn(Collections.singleton(itemRequestDto));
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
        verify(itemRequestService).getByUser(1L);
    }

    @Test
    void shouldGetByWrongUser() throws Exception {
        when(itemRequestService.getByUser(1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(NotFoundException.class)));
        verify(itemRequestService).getByUser(1L);
    }

    @Test
    void shouldGetAll() throws Exception {
        when(itemRequestService.getAllByUserId(1L, CustomPageable.of(0, 5)))
                .thenReturn(Collections.singleton(itemRequestDto));
        mockMvc.perform(get(url + "/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
        verify(itemRequestService).getAllByUserId(1L, CustomPageable.of(0, 5));
    }

    @Test
    void shouldGetAllWithIncorrectId() throws Exception {
        when(itemRequestService.getAllByUserId(1L, CustomPageable.of(0, 5)))
                .thenThrow(NotFoundException.class);
        mockMvc.perform(get(url + "/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1)
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                        is(NotFoundException.class)));
        verify(itemRequestService).getAllByUserId(1L, CustomPageable.of(0, 5));
    }
}
