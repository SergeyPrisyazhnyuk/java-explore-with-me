package ru.practicum.ewm.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.model.User;

@UtilityClass
public class UserMapper {

    public User toUser(NewUserRequest request) {

        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .build();
    }

    public UserDto toUserDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {

        return UserShortDto.builder()
                .name(user.getName())
                .build();

    }

}
