package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repository.UserRepository;
import ru.practicum.ewm.utility.CheckUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final CheckUtil checkUtil;

    @Override
    public List<UserDto> getUsers(List<Long> userIds, Integer from, Integer size) {

        PageRequest pageRequest = PageRequest.of(from / size, size);

        List<UserDto> userDto;

        if (userIds != null) {
          userDto = userRepository.findByIdIn(userIds, pageRequest).stream()
                  .map(UserMapper::toUserDto)
                  .collect(Collectors.toList());
        } else {
          userDto = userRepository.findAll(pageRequest).stream()
                  .map(UserMapper::toUserDto)
                  .collect(Collectors.toList());
        }

        return userDto;
    }

    @Override
    @Transactional
    public UserDto addUser(NewUserRequest newUserRequest) {
        User user = userRepository.save(UserMapper.toUser(newUserRequest));
        return UserMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = checkUtil.checkUserId(userId);
        userRepository.delete(user);
    }
}
