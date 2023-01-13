package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.util.Create;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class UserDto {
    @Positive
    private long id;
    @NotBlank(groups = {Create.class})
    private String name;
    @Email(groups = {Create.class})
    @NotBlank(groups = {Create.class})
    private String email;
}