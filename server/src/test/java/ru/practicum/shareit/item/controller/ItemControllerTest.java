package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    private ItemDto itemDto;
    private CommentDto commentDto;
    private final String url = "/items";
    private final String header = "X-Sharer-User-Id";

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        itemDto = ItemDto.builder()
                .id(1L).name("item").description("test").available(true).build();
        commentDto = CommentDto.builder().id(1L).created(LocalDateTime.now()).text("text").authorName("user").build();
    }

    @Test
    void shouldCreateUser() throws Exception {
        when(itemService.createItem(itemDto, 1L)).thenReturn(itemDto);
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService).createItem(itemDto, 1L);
    }

    @Test
    void shouldCreateItemWithIncorrectUserId() throws Exception {
        when(itemService.createItem(itemDto, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(post(url)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                                is(NotFoundException.class)));
        verify(itemService).createItem(itemDto, 1L);
    }

    @Test
    void shouldGetUsersItems() throws Exception {
        when(itemService.getUserItems(1L, 0, 5))
                .thenReturn(Collections.singleton(itemDto));
        mockMvc.perform(get(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("size", "5")
                        .param("from", "0")
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
        verify(itemService).getUserItems(1L, 0, 5);
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getItemByOwner(1L, 1L)).thenReturn(itemDto);
        mockMvc.perform(get(url + "/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService).getItemByOwner(1L, 1L);
    }

    @Test
    void shouldUpdateItem() throws Exception {
        itemDto.setName("update");
        when(itemService.updateItem(itemDto, 1L, 1L)).thenReturn(itemDto);
        mockMvc.perform(patch(url + "/{id}", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(itemService).updateItem(itemDto, 1L, 1L);
    }

    @Test
    void shouldUpdateWithIncorrectId() throws Exception {
        itemDto.setName("update");
        when(itemService.updateItem(itemDto, 1L, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(patch(url + "/{id}", 1)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isNotFound());
        verify(itemService).updateItem(itemDto, 1L, 1L);
    }

    @Test
    void shouldSearchItems() throws Exception {
        when(itemService.searchItems("item", 0, 5))
                .thenReturn(Collections.singleton(itemDto));
        mockMvc.perform(get(url + "/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)));
        verify(itemService).searchItems("item", 0, 5);
    }

    @Test
    void shouldCreateComment() throws Exception {
        when(itemService.createComment(commentDto, 1L, 1L)).thenReturn(commentDto);
        mockMvc.perform(post(url + "/{id}/" + "comment", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is(commentDto.getText())));
        verify(itemService).createComment(commentDto, 1L, 1L);
    }

    @Test
    void shouldCreateCommentWithIncorrectId() throws Exception {
        when(itemService.createComment(commentDto, 1L, 1L)).thenThrow(NotFoundException.class);
        mockMvc.perform(post(url + "/{id}/" + "comment", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(header, 1))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertThat(Objects.requireNonNull(result.getResolvedException()).getClass(),
                                is(NotFoundException.class)));
        verify(itemService).createComment(commentDto, 1L, 1L);
    }
}
