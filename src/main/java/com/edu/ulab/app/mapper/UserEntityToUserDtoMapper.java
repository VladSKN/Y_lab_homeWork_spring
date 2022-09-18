package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.BookEntity;
import com.edu.ulab.app.entity.UserEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserEntityToUserDtoMapper {

    UserDto userEntityToUserDto(UserEntity userEntity);

    UserEntity userDtoToUserEntity(UserDto userDto);

    List<BookEntity> map(List<BookDto> bookDtoList);
}
