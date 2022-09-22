package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.mapper.UserEntityToUserDtoMapper;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.storage.BookRepository;
import com.edu.ulab.app.storage.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private static final AtomicLong id = new AtomicLong(System.currentTimeMillis());

    private final UserRepository userRepository;

    private final BookRepository bookRepository;

    private final UserEntityToUserDtoMapper toUserDtoMapper;

    public UserServiceImpl(UserRepository userRepository,
                           BookRepository bookRepository,
                           UserEntityToUserDtoMapper toUserDtoMapper) {
        this.userRepository = userRepository;
        this.bookRepository = bookRepository;
        this.toUserDtoMapper = toUserDtoMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        // сгенерировать идентификатор
        // создать пользователя
        // вернуть сохраненного пользователя со всеми необходимыми полями id
        UserEntity userEntity = toUserDtoMapper.userDtoToUserEntity(userDto);
        userEntity.setId(id.getAndIncrement());

        userRepository.createUser(userEntity);

       // userEntity.setBookList(bookRepository.findBookByUserId(userEntity.getId()));

        UserDto userEntityToUserDto = toUserDtoMapper.userEntityToUserDto(userEntity);

        log.info("createUser from UserServiceImpl successfully: {}", userEntityToUserDto);
        return userEntityToUserDto;
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntity = toUserDtoMapper.userDtoToUserEntity(userDto);
        userRepository.updateUser(userEntity);

        log.info("updateUser from UserServiceImpl successfully: {}", userDto);
        return userDto;
    }

    @Override
    public UserDto getUserById(Long id) {
        UserEntity userById = userRepository.getUserById(id);
        UserDto userDto = toUserDtoMapper.userEntityToUserDto(userById);

        log.info("getUserById from UserServiceImpl successfully: {}", userDto);
        return userDto;
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteUserById(id);

        log.info("deleteUserById from UserServiceImpl successfully: {}", id);
    }

    @Override
    public UserDto getUserByName(String name) {
        UserEntity userByName = userRepository.getUserByName(name);

        UserDto userDto = toUserDtoMapper.userEntityToUserDto(userByName);

        log.info("getUserByName from UserServiceImpl successfully: {}", userDto);
        return userDto;
    }
}
