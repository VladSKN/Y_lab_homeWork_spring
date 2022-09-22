package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserEntityToUserDtoMapper;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.service.BookService;
import com.edu.ulab.app.service.UserService;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class UserDataFacade {
    private final UserService userService;
    private final BookService bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    private final UserEntityToUserDtoMapper userEntityToUserDtoMapper;

    public UserDataFacade(UserService userService,
                          BookService bookService,
                          UserMapper userMapper,
                          BookMapper bookMapper,
                          UserEntityToUserDtoMapper userEntityToUserDtoMapper) {
        this.userService = userService;
        this.bookService = bookService;
        this.userMapper = userMapper;
        this.bookMapper = bookMapper;
        this.userEntityToUserDtoMapper = userEntityToUserDtoMapper;
    }

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Long> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();

        UserEntity userEntity = userEntityToUserDtoMapper.userDtoToUserEntity(createdUser);
        userEntity.setBookList(bookIdList);

        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest) {
        UserDto userByName = userService.getUserByName(userBookRequest.getUserRequest().getFullName());

        UserBookResponse build = UserBookResponse.builder()
                .userId(userByName.getId())
                .booksIdList(getListBookLong(userByName.getId()))
                .build();

        userService.updateUser(userMapper.userRequestToUserDto(userBookRequest.getUserRequest()));

        log.info("updateUserWithBooks: {}", build);

        return build;
    }

    public UserBookResponse getUserWithBooks(Long userId) {
        List<Long> list = getListBookLong(userId);

        UserBookResponse build = UserBookResponse.builder()
                .userId(userId)
                .booksIdList(list)
                .build();

        log.info("getUserWithBooks: {}", build);

        return build;
    }

    public void deleteUserWithBooks(Long userId) {
        bookService.deleteBookByUserId(userId);
        userService.deleteUserById(userId);

        log.info("deleteUserWithBooks");
    }

    private List<Long> getListBookLong(Long userId) {
        List<BookEntity> bookByUserId = bookService.findBookByUserId(userId);

        return bookByUserId.stream()
                .map(BookEntity::getUserId)
                .toList();
    }
}
